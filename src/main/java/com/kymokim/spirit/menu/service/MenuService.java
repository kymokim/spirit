package com.kymokim.spirit.menu.service;

import com.kymokim.spirit.common.service.S3Service;
import com.kymokim.spirit.menu.dto.RequestMenu;
import com.kymokim.spirit.menu.dto.ResponseMenu;
import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.menu.repository.MenuRepository;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    private final S3Service s3Service;

    public Long createMenu(RequestMenu.CreateMenuDto createMenuDto) {
        Store store = storeRepository.findById(createMenuDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }
        Menu menu = RequestMenu.CreateMenuDto.toEntity(createMenuDto, store);
        menuRepository.save(menu);
        store.addMenu(menu);
        return menu.getId();
    }

    public String uploadImg(MultipartFile file, long menuId){
        Menu menu = menuRepository.findById(menuId).get();

//        if (!store.getImgUrl().isEmpty())
//            s3Service.deleteFile(store.getImgUrl());

        String url = "";
        url = s3Service.upload(file,"store");

        menu.setImgUrl(url);
        menuRepository.save(menu);
        return url;
    }

    public List<ResponseMenu.MenuListDto> getByStoreId(Long storeId){
        Store store = storeRepository.findById(storeId).get();
        List<ResponseMenu.MenuListDto> menuList = new ArrayList<>();
        if(!store.getMenuList().isEmpty())
            store.getMenuList().stream().forEach(menu -> menuList.add(ResponseMenu.MenuListDto.toDto(menu)));
        return menuList;
    }

    public List<ResponseMenu.GetAllMenuDto> getAllMenu() {
        List<Menu> entityList = menuRepository.findAll();
        List<ResponseMenu.GetAllMenuDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(menu -> dtoList.add(ResponseMenu.GetAllMenuDto.toDto(menu)));
        return dtoList;
    }

    public ResponseMenu.GetMenuDto getMenu(Long id) {
        Menu menu = menuRepository.findById(id).get();
        return ResponseMenu.GetMenuDto.toDto(menu);
    }

    public void updateMenu(Long menuId, RequestMenu.UpdateMenuDto updateMenuDto) {
        Menu originalMenu = menuRepository.findById(menuId).get();
        Menu updatedMenu = RequestMenu.UpdateMenuDto.toEntity(originalMenu, updateMenuDto);
        menuRepository.save(updatedMenu);
    }

    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).get();
        menuRepository.delete(menu);
    }
}
