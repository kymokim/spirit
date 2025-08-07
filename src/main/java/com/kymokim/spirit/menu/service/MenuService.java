package com.kymokim.spirit.menu.service;

import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.menu.dto.RequestMenu;
import com.kymokim.spirit.menu.dto.ResponseMenu;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.menu.exception.MenuErrorCode;
import com.kymokim.spirit.menu.repository.MenuRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import com.kymokim.spirit.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@MainTransactional
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;
    private final StoreService storeService;

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Menu resolveMenu(Long menuId){
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(MenuErrorCode.MENU_NOT_FOUND));
    }

    public void createMenu(MultipartFile file, RequestMenu.CreateMenuDto createMenuDto) {
        Store store = resolveStore(createMenuDto.getStoreId());
        storeService.validateStoreAccess(store.getId());
        Integer maxOrder = menuRepository.findMaxSortOrderByStoreId(store.getId()).orElse(-1);
        Menu menu = createMenuDto.toEntity(store, maxOrder + 1, AuthResolver.resolveUserId());
        String imageUrl;
        if (file != null){
            imageUrl = s3Service.upload(file, "menu/" + String.valueOf(menu.getId()));
            menu.setImgUrl(imageUrl);
        }
        menuRepository.save(menu);
        store.addMenuList(menu);
        storeRepository.save(store);
    }

    public void updateImage(MultipartFile file, Long menuId){
        Menu menu = resolveMenu(menuId);
        storeService.validateStoreAccess(menu.getStore().getId());
        String imageUrl;
        if (file != null){
            if (menu.getImgUrl() == null) {
                imageUrl = s3Service.upload(file, "menu/" + String.valueOf(menu.getId()));
            }
            else {
                imageUrl = s3Service.update(file, "menu/" + String.valueOf(menu.getId()), menu.getImgUrl());
            }
        } else {
            throw new CustomException(MenuErrorCode.MENU_IMG_FILE_EMPTY);
        }
        menu.setImgUrl(imageUrl);
        menu.getHistoryInfo().update(AuthResolver.resolveUserId());
        menuRepository.save(menu);
    }

    public void deleteImage(Long menuId){
        Menu menu = resolveMenu(menuId);
        storeService.validateStoreAccess(menu.getStore().getId());
        String originUrl;
        if (!Objects.equals(menu.getImgUrl(), null) && !menu.getImgUrl().isEmpty()){
            originUrl = menu.getImgUrl();
        } else {
            throw new CustomException(MenuErrorCode.MENU_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(originUrl);
        menu.setImgUrl(null);
        menuRepository.save(menu);
    }

    @MainTransactional(readOnly = true)
    public List<ResponseMenu.MenuListDto> getByStore(Long storeId){
        return TransactionRetryUtil.executeWithRetry(() -> {
            Store store = resolveStore(storeId);
            List<ResponseMenu.MenuListDto> menuList = new ArrayList<>();
            if(!store.getMenuList().isEmpty())
                store.getMenuList().forEach(menu -> menuList.add(ResponseMenu.MenuListDto.toDto(menu)));
            return menuList;
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public ResponseMenu.GetMenuDto getMenu(Long menuId) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Menu menu = resolveMenu(menuId);
            return ResponseMenu.GetMenuDto.toDto(menu);
        }, 3);
    }

    public void updateMenu(Long menuId, RequestMenu.UpdateMenuDto updateMenuDto) {
        Menu originalMenu = resolveMenu(menuId);
        storeService.validateStoreAccess(originalMenu.getStore().getId());
        Menu updatedMenu = updateMenuDto.toEntity(originalMenu);
        updatedMenu.getHistoryInfo().update(AuthResolver.resolveUserId());
        menuRepository.save(updatedMenu);
    }

    public void updateMenuSortOrder(RequestMenu.UpdateMenuSortOrderDto updateMenuSortOrderDto) {
        storeService.validateStoreAccess(updateMenuSortOrderDto.getStoreId());
        List<Long> menuIdInOrderList = updateMenuSortOrderDto.getMenuIdInOrderList();
        List<Menu> menus = menuRepository.findAllById(menuIdInOrderList);

        for (int i = 0; i < menuIdInOrderList.size(); i++) {
            Long menuId = menuIdInOrderList.get(i);
            Menu menu = menus.stream()
                    .filter(menu1 -> menu1.getId().equals(menuId))
                    .findFirst()
                    .orElseThrow(() -> new CustomException(MenuErrorCode.MENU_NOT_FOUND));

            if (!menu.getStore().getId().equals(updateMenuSortOrderDto.getStoreId())) {
                throw new CustomException(MenuErrorCode.INVALID_MENU_STORE_RELATION);
            }
            menu.setSortOrder(i);
        }
    }

    public void deleteMenu(Long menuId) {
        Menu menu = resolveMenu(menuId);
        Store store = resolveStore(menu.getStore().getId());
        storeService.validateStoreAccess(store.getId());
        if (!Objects.equals(menu.getImgUrl(), null) && !menu.getImgUrl().isEmpty()){
            s3Service.deleteFile(menu.getImgUrl());
        }
        store.removeMenuList(menu);
        menuRepository.delete(menu);
        storeRepository.save(store);
    }
}
