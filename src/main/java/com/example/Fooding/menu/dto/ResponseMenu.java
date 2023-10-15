package com.example.Fooding.menu.dto;

import com.example.Fooding.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;

public class ResponseMenu {

    @Builder
    @Getter
    public static class GetMenuDto {
        private Long id;
        private String menuName;
        private String menuContent;
        private Long price;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .id(menu.getId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetAllMenuDto {
        private Long id;
        private String menuName;
        private String menuContent;
        private Long price;

        public static GetAllMenuDto toDto(Menu menu) {
            return GetAllMenuDto.builder()
                    .id(menu.getId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class storeIdMenuDto {
        private Long id;
        private String menuName;
        private String menuContent;
        private Long price;

        public static storeIdMenuDto toDto(Menu menu) {
            return storeIdMenuDto.builder()
                    .id(menu.getId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .build();
        }
    }
}
