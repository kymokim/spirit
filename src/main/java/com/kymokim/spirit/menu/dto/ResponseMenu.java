package com.kymokim.spirit.menu.dto;

import com.kymokim.spirit.menu.entity.Menu;
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
        private String imgUrl;
        private boolean isMain;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .storeId(menu.getStore().getStoreId())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MenuListDto {
        private Long menuId;
        private String menuName;
        private String menuContent;
        private Long price;
        private Long menuLikeCount;
        private String imgUrl;
        private Boolean isMain;

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
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
        private String imgUrl;
        private boolean isMain;

        public static GetAllMenuDto toDto(Menu menu) {
            return GetAllMenuDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .storeId(menu.getStore().getStoreId())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }
}
