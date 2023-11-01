package com.example.Fooding.menu.service;

import com.example.Fooding.menu.dto.RequestMenu;
import com.example.Fooding.menu.dto.ResponseMenu;
import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.menu.repository.MenuRepository;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;
    public void createMenu(RequestMenu.CreateMenuDto createMenuDto) {
        Store store = storeRepository.findById(createMenuDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }
        Menu menu = RequestMenu.CreateMenuDto.toEntity(createMenuDto, store);
        menuRepository.save(menu);
        store.addMenus(menu);
    }

    public List<ResponseMenu.GetAllMenuDto> getAllMenu() {
        List<Menu> tasks = menuRepository.findAll();
        List<ResponseMenu.GetAllMenuDto> list = new ArrayList<>();
        tasks.stream().forEach(task -> list.add(ResponseMenu.GetAllMenuDto.toDto(task)));
        return list;
    }

    public ResponseMenu.GetMenuDto getMenu(Long id) {
        Menu menu = menuRepository.findById(id).get();
        return ResponseMenu.GetMenuDto.toDto(menu);
    }

    public List<ResponseMenu.storeIdMenuDto> getById(Long storeId) {
        //List<Menu> menus = menuRepository.findAllByStoreId(storeId);
        List<Menu> menus = menuRepository.findAll();
        List<ResponseMenu.storeIdMenuDto> dtoList = new ArrayList<>();
        menus.stream().forEach(menu -> dtoList.add(ResponseMenu.storeIdMenuDto.toDto(menu)));
        return dtoList;
    }

    public void updateMenu(RequestMenu.UpdateMenuDto updateMenuDto) {
        Menu originalMenu = menuRepository.findById(updateMenuDto.getId()).get();
        Menu updatedMenu = RequestMenu.UpdateMenuDto.toEntity(originalMenu, updateMenuDto);
        menuRepository.save(updatedMenu);
    }

    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).get();
        menuRepository.delete(menu);
    }
}
