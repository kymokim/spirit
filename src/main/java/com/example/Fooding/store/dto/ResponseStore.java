package com.example.Fooding.store.dto;

import com.example.Fooding.menu.entity.Menu;
import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseStore {

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long makerId;
        private Long ownerId;
        private String storeName;
        private String category;
        private String address;
        private String storeNumber;
        private String storeContent;
        private String imgUrl;
        private String longitude;
        private String latitude;
        private String openHour;
        private String closeHour;
        private Double totalRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private Double rateAvg; // 추가
        private List<MenuListDto> menuList;

        public static GetStoreDto toDto(Store store, Double rateAvg) {

            List<MenuListDto> menuList = new ArrayList<>();
            if(!store.getMenuList().isEmpty())
                store.getMenuList().stream().forEach(menu -> menuList.add(MenuListDto.toDto(menu)));

            return GetStoreDto.builder()
                    .makerId(store.getMakerId())
                    .ownerId(store.getOwnerId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .address(store.getAddress())
                    .storeNumber(store.getStoreNumber())
                    .storeContent(store.getStoreContent())
                    .imgUrl(store.getImgUrl())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .rateAvg(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .menuList(menuList)
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

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllStoreDto {
        private Long storeId;
        private String storeName;
        private String category;
        private String address;
        private String imgUrl;
        private String openHour;
        private String closeHour;
        private String longitude;
        private String latitude;
        private Double totalRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private Double rateAvg; // 추가

        public static GetAllStoreDto toDto(Store store, Double rateAvg) {
            return GetAllStoreDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .address(store.getAddress())
                    .imgUrl(store.getImgUrl())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .rateAvg(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .build();
        }
    }

}
