package com.example.Fooding.menu.service;

import com.example.Fooding.common.service.S3Service;
import com.example.Fooding.menu.dto.RequestMenu;
import com.example.Fooding.menu.dto.ResponseMenu;
import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.menu.repository.MenuRepository;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
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
        return menu.getMenuId();
    }

    public String uploadImg(MultipartFile file, long menuId){
        Menu menu = menuRepository.findById(menuId).get();

//        if (!store.getImgUrl().isEmpty())
//            s3Service.deleteFile(store.getImgUrl());

        String url = "";
        try {
            url = s3Service.upload(file,"store");
        }
        catch (IOException e){
            System.out.println("S3 upload failed.");
        }

        menu.setImgUrl(url);
        menuRepository.save(menu);
        return url;
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

    public void updateMenu(RequestMenu.UpdateMenuDto updateMenuDto) {
        Menu originalMenu = menuRepository.findById(updateMenuDto.getMenuId()).get();
        Menu updatedMenu = RequestMenu.UpdateMenuDto.toEntity(originalMenu, updateMenuDto);
        menuRepository.save(updatedMenu);
    }

    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).get();
        menuRepository.delete(menu);
    }
}
