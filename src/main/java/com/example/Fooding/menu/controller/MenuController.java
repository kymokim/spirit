package com.example.Fooding.menu.controller;

import com.example.Fooding.common.dto.ResponseDto;
import com.example.Fooding.menu.service.MenuService;
import com.example.Fooding.menu.dto.RequestMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {
    @Autowired
    private final MenuService menuService;

    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createMenu(@RequestBody RequestMenu.CreateMenuDto createMenuDto) {

        menuService.createMenu(createMenuDto);

        ResponseDto responseDto = ResponseDto.builder()
                .message("Menu created successfully.")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
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
