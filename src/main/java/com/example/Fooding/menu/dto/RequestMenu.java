package com.example.Fooding.menu.dto;

import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;

public class RequestMenu {

    @Data
    @Builder
    public static class CreateMenuDto {
        private String menuName;
        private String menuContent;
        private Long price;
        private Long storeId;

        public static Menu toEntity(CreateMenuDto createMenuDto, Store store) {
            return Menu.builder()
                    .menuName(createMenuDto.getMenuName())
                    .menuContent(createMenuDto.getMenuContent())
                    .price(createMenuDto.getPrice())
                    .store(store)
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
