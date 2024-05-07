package com.kymokim.spirit.menu.controller;

import com.kymokim.spirit.common.dto.ResponseDto;
import com.kymokim.spirit.menu.dto.ResponseMenu;
import com.kymokim.spirit.menu.service.MenuService;
import com.kymokim.spirit.menu.dto.RequestMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private final MenuService menuService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createMenu(@RequestBody RequestMenu.CreateMenuDto createMenuDto) {

        Long menuId = menuService.createMenu(createMenuDto);
        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu created successfully.")
                .data(menuId)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/uploadImg/{menuId}")
    public ResponseEntity<ResponseDto> uploadMenuImg(@RequestPart(value = "file", required = false) MultipartFile file,
                                                      @PathVariable("menuId") Long menuId){
        String url = menuService.uploadImg(file, menuId);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Image uploaded successfully.")
                .data(url)
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
