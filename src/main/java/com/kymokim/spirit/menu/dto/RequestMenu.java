package com.kymokim.spirit.menu.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestMenu {

    @Data
    @Builder
    public static class CreateMenuDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private Long price;
        @NotEmpty
        private Long storeId;
        @NotEmpty
        private Boolean isMain;

        public static Menu toEntity(CreateMenuDto createMenuDto, Store store) {
            return Menu.builder()
                    .name(createMenuDto.getName())
                    .description(createMenuDto.getDescription())
                    .price(createMenuDto.getPrice())
                    .isMain(createMenuDto.getIsMain())
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateMenuDto {
        @NotEmpty
        private String name;
        private String description;
        @NotEmpty
        private Long price;
        @NotEmpty
        private Boolean isMain;

        public static Menu toEntity(Menu menu, UpdateMenuDto updateMenuDto) {
            menu.update(updateMenuDto.getName(), updateMenuDto.getDescription(), updateMenuDto.getPrice(), updateMenuDto.getIsMain());
            return menu;
        }
    }
}
