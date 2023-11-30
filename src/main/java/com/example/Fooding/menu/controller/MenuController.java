package com.example.Fooding.menu.controller;

import com.amazonaws.Response;
import com.example.Fooding.auth.security.JwtAuthTokenProvider;
import com.example.Fooding.common.dto.ResponseDto;
import com.example.Fooding.menu.dto.ResponseMenu;
import com.example.Fooding.menu.service.MenuService;
import com.example.Fooding.menu.dto.RequestMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private final MenuService menuService;
    private final JwtAuthTokenProvider jwtAuthTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createMenu(@RequestBody RequestMenu.CreateMenuDto createMenuDto) {

        menuService.createMenu(createMenuDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/like/{menuId}")
    public ResponseEntity<ResponseDto> likeMenu(@PathVariable("menuId") Long menuId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        menuService.likeMenu(menuId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu liked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/unlike/{menuId}")
    public ResponseEntity<ResponseDto> unlikeMenu(@PathVariable("menuId") Long menuId, HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        menuService.unlikedMenu(menuId, token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu unliked successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get")
    public ResponseEntity<ResponseDto> getAllMenu() {

        List<ResponseMenu.GetAllMenuDto> response = menuService.getAllMenu();
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseDto> getMenu(@PathVariable("id") Long id) {

        ResponseMenu.GetMenuDto response = menuService.getMenu(id);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/getLikedMenu")
    public ResponseEntity<ResponseDto> getLikedMenu(HttpServletRequest request) {
        Optional<String> token = jwtAuthTokenProvider.getAuthToken(request);
        List<ResponseMenu.GetLikedMenuDto> response = menuService.getLikedMenu(token);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Liked menu list retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateMenu(@RequestBody RequestMenu.UpdateMenuDto updateMenuDto) {

        menuService.updateMenu(updateMenuDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<ResponseDto> deleteMenu(@PathVariable("menuId") Long menuId) {

        menuService.deleteMenu(menuId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu deleted successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
