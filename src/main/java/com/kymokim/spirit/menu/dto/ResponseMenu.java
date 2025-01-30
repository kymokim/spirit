package com.kymokim.spirit.menu.dto;

import com.kymokim.spirit.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;

public class ResponseMenu {

    @Builder
    @Getter
    public static class GetMenuDto {
        private Long id;
        private String name;
        private String description;
        private String price;
        private Long storeId;
        private String imgUrl;
        private Boolean isMain;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .storeId(menu.getStore().getId())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MenuListDto {
        private Long id;
        private String name;
        private String description;
        private String price;
        private String imgUrl;
        private Boolean isMain;

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }
}
