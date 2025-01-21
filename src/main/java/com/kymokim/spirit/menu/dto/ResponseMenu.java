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
        private Long price;
        private Long likeCount;
        private Long storeId;
        private String imgUrl;
        private boolean isMain;

        public static GetMenuDto toDto(Menu menu) {
            return GetMenuDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .likeCount(menu.getLikeCount())
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
        private Long price;
        private Long likeCount;
        private String imgUrl;
        private Boolean isMain;

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .likeCount(menu.getLikeCount())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetAllMenuDto {
        private Long id;
        private String name;
        private String description;
        private Long price;
        private Long likeCount;
        private Long storeId;
        private String imgUrl;
        private boolean isMain;

        public static GetAllMenuDto toDto(Menu menu) {
            return GetAllMenuDto.builder()
                    .id(menu.getId())
                    .name(menu.getName())
                    .description(menu.getDescription())
                    .price(menu.getPrice())
                    .likeCount(menu.getLikeCount())
                    .storeId(menu.getStore().getId())
                    .imgUrl(menu.getImgUrl())
                    .isMain(menu.getIsMain())
                    .build();
        }
    }
}
