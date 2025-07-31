package com.kymokim.spirit.main.menu.dto;

import com.kymokim.spirit.main.menu.entity.Menu;
import com.kymokim.spirit.main.menu.entity.MenuType;
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
        private MenuType menuType;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .storeId(menu.getStore().getId())
                    .imgUrl(menu.getImgUrl())
                    .menuType(menu.getMenuType())
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
        private MenuType menuType;

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .imgUrl(menu.getImgUrl())
                    .menuType(menu.getMenuType())
                    .build();
        }
    }
}
