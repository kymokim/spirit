package com.example.Fooding.menu.service;

import com.example.Fooding.auth.repository.AuthRepository;
import com.example.Fooding.auth.security.JwtAuthToken;
import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.common.service.S3Service;
import com.example.Fooding.menu.dto.RequestMenu;
import com.example.Fooding.menu.dto.ResponseMenu;
import com.example.Fooding.menu.entity.LikedMenu;
import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.menu.repository.LikedMenuRepository;
import com.example.Fooding.menu.repository.MenuRepository;
import com.example.Fooding.store.entity.Store;
import com.example.Fooding.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    private final LikedMenuRepository likedMenuRepository;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;
    private final AuthRepository authRepository;
    private final S3Service s3Service;
    private final StoreRepository storeRepository;

    public void createMenu(RequestMenu.CreateMenuDto createMenuDto) {
        Store store = storeRepository.findById(createMenuDto.getStoreId()).get();
        if(store == null) {
            throw new EntityNotFoundException();
        }
        Menu menu = RequestMenu.CreateMenuDto.toEntity(createMenuDto, store);
        menuRepository.save(menu);
        store.addMenu(menu);
    }

    public void likeMenu(Long menuId, Optional<String> token){
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        LikedMenu likedMenu = LikedMenu.builder()
                .menuId(menuId)
                .userId(userId)
                .build();
        likedMenuRepository.save(likedMenu);
        Menu menu = menuRepository.findById(menuId).get();
        menu.increaseMenuLikeCount();
        menuRepository.save(menu);
    }

    public void unlikedMenu(Long menuId, Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        LikedMenu likedMenu = likedMenuRepository.findByUserIdAndMenuId(userId, menuId);
        likedMenuRepository.delete(likedMenu);
        Menu menu = menuRepository.findById(menuId).get();
        menu.decreaseMenuLikeCount();
        menuRepository.save(menu);
    }

    public List<ResponseMenu.GetAllMenuDto> getAllMenu() {
        List<Menu> entityList = menuRepository.findAll();
        List<ResponseMenu.GetAllMenuDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(menu -> dtoList.add(ResponseMenu.GetAllMenuDto.toDto(menu)));
        return dtoList;
    }

    public List<ResponseMenu.GetLikedMenuDto> getLikedMenu(Optional<String> token) {
        String email = null;
        if (token.isPresent()) {
            JwtAuthToken jwtAuthToken = jwtAuthTokenProvider.convertAuthToken(token.get());
            email = jwtAuthToken.getClaims().getSubject();
        }
        Long userId = authRepository.findByEmail(email).getId();
        List<LikedMenu> entityList = likedMenuRepository.findAllByUserId(userId);
        List<ResponseMenu.GetLikedMenuDto> dtoList = new ArrayList<>();
        entityList.stream().forEach(likedMenu -> {
            Menu menu = menuRepository.findById(likedMenu.getMenuId()).get();
            dtoList.add(ResponseMenu.GetLikedMenuDto.toDto(menu));
        });
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
