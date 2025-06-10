package com.kymokim.spirit.menu.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.common.security.JwtTokenProvider;
import com.kymokim.spirit.menu.dto.ResponseMenu;
import com.kymokim.spirit.menu.service.MenuService;
import com.kymokim.spirit.menu.dto.RequestMenu;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Menu API")
@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private final MenuService menuService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> createMenu(@RequestPart(value = "file", required = false) MultipartFile file,
                                                  @RequestPart(value = "createMenuDto") RequestMenu.CreateMenuDto createMenuDto) {
        menuService.createMenu(file, createMenuDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu created successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping(value = "/update-image/{menuId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto> updateMenuImage(@RequestPart(value = "file", required = true) MultipartFile file,
                                                       @PathVariable("menuId") Long menuId){
        menuService.updateImage(file, menuId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image updated successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("delete-image/{menuId}")
    public ResponseEntity<ResponseDto> deleteImage(@PathVariable("menuId") Long menuId){
        menuService.deleteImage(menuId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Image deleted successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/store/{storeId}")
    public ResponseEntity<ResponseDto> getByStore(@PathVariable("storeId") Long storeId) {
        List<ResponseMenu.MenuListDto> response = menuService.getByStore(storeId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/get-by/{menuId}")
    public ResponseEntity<ResponseDto> getMenu(@PathVariable("menuId") Long menuId) {
        ResponseMenu.GetMenuDto response = menuService.getMenu(menuId);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu retrieved successfully.")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/update/{menuId}")
    public ResponseEntity<ResponseDto> updateMenu(@PathVariable("menuId") Long menuId, @RequestBody RequestMenu.UpdateMenuDto updateMenuDto) {
        menuService.updateMenu(menuId, updateMenuDto);
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
