package com.example.Fooding.menu.dto;

import com.example.Fooding.menu.entity.Menu;
import lombok.Builder;
import lombok.Data;

public class RequestMenu {

    @Data
    @Builder
    public static class CreateMenuDto {
        private String menuName;
        private String menuContent;
        private Long price;

        public static Menu toEntity(CreateMenuDto createMenuDto) {
            return Menu.builder()
                    .menuName(createMenuDto.getMenuName())
                    .menuContent(createMenuDto.getMenuContent())
                    .price(createMenuDto.getPrice())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateMenuDto {
        private Long id;
        private String menuName;
        private String menuContent;
        private Long price;

        public static Menu toEntity(Menu menu, UpdateMenuDto updateMenuDto) {
            menu.update(updateMenuDto.getMenuName(), updateMenuDto.getMenuContent(), updateMenuDto.getPrice());
            return menu;
        }
    }
}
