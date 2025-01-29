package com.kymokim.spirit.menu.service;

import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.menu.dto.RequestMenu;
import com.kymokim.spirit.menu.dto.ResponseMenu;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.menu.exception.MenuErrorCode;
import com.kymokim.spirit.menu.repository.MenuRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    private Store resolveStore(Long storeId){
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Menu resolveMenu(Long menuId){
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new CustomException(MenuErrorCode.MENU_NOT_FOUND));
    }

    @Transactional
    public void createMenu(MultipartFile file, RequestMenu.CreateMenuDto createMenuDto) {
        Store store = resolveStore(createMenuDto.getStoreId());
        Menu menu = RequestMenu.CreateMenuDto.toEntity(createMenuDto, store);
        String imageUrl;
        if (file != null){
            imageUrl = s3Service.upload(file, "menu/" + String.valueOf(menu.getId()));
            menu.setImgUrl(imageUrl);
        }
        menuRepository.save(menu);
        store.addMenuList(menu);
        storeRepository.save(store);
    }

    @Transactional
    public void updateImage(MultipartFile file, Long menuId){
        Menu menu = resolveMenu(menuId);
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
        menuRepository.save(menu);
    }

    @Transactional
    public void deleteImage(Long menuId){
        Menu menu = resolveMenu(menuId);
        String originUrl;
        if (!(menu.getImgUrl() == null) && !menu.getImgUrl().isEmpty()){
            originUrl = menu.getImgUrl();
        } else {
            throw new CustomException(MenuErrorCode.MENU_ORIGIN_IMG_URL_EMPTY);
        }
        s3Service.deleteFile(originUrl);
        menu.setImgUrl(null);
        menuRepository.save(menu);
    }

    @Transactional
    public List<ResponseMenu.MenuListDto> getByStore(Long storeId){
        Store store = resolveStore(storeId);
        List<ResponseMenu.MenuListDto> menuList = new ArrayList<>();
        if(!store.getMenuList().isEmpty())
            store.getMenuList().forEach(menu -> menuList.add(ResponseMenu.MenuListDto.toDto(menu)));
        return menuList;
    }

    @Transactional
    public ResponseMenu.GetMenuDto getMenu(Long menuId) {
        Menu menu = resolveMenu(menuId);
        return ResponseMenu.GetMenuDto.toDto(menu);
    }

    @Transactional
    public void updateMenu(Long menuId, RequestMenu.UpdateMenuDto updateMenuDto) {
        Menu originalMenu = resolveMenu(menuId);
        Menu updatedMenu = RequestMenu.UpdateMenuDto.toEntity(originalMenu, updateMenuDto);
        menuRepository.save(updatedMenu);
    }

    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = resolveMenu(menuId);
        menuRepository.delete(menu);
    }
}
