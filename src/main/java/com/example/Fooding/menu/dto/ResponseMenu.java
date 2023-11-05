package com.example.Fooding.menu.dto;

import com.example.Fooding.menu.entity.Menu;
import lombok.Builder;
import lombok.Getter;

public class ResponseMenu {

    @Builder
    @Getter
    public static class GetMenuDto {
        private Long menuId;
        private String menuName;
        private String menuContent;
        private Long price;
        private Long menuLikeCount;
        private Long storeId;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .storeId(menu.getStore().getStoreId())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetAllMenuDto {
        private Long menuId;
        private String menuName;
        private String menuContent;
        private Long price;
        private Long menuLikeCount;
        private Long storeId;

        public static GetAllMenuDto toDto(Menu menu) {
            return GetAllMenuDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .storeId(menu.getStore().getStoreId())
                    .build();
        }
    }
}
