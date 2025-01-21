package com.kymokim.spirit.menu.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

public class RequestMenu {

    @Data
    @Builder
    public static class CreateMenuDto {
        private String name;
        private String description;
        private Long price;
        private Long storeId;
        private boolean isMain;

        public static Menu toEntity(CreateMenuDto createMenuDto, Store store) {
            return Menu.builder()
                    .name(createMenuDto.getName())
                    .description(createMenuDto.getDescription())
                    .price(createMenuDto.getPrice())
                    .isMain(createMenuDto.isMain())
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateMenuDto {
        private String name;
        private String description;
        private Long price;
        private boolean isMain;

        public static Menu toEntity(Menu menu, UpdateMenuDto updateMenuDto) {
            menu.update(updateMenuDto.getName(), updateMenuDto.getDescription(), updateMenuDto.getPrice(), updateMenuDto.isMain());
            return menu;
        }
    }
}
