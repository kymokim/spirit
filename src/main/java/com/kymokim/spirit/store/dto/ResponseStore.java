package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResponseStore {

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long writerId;
        private Long ownerId;
        private String storeName;
        private String firstCategory;
        private String secondCategory;
        private String thirdCategory;
        private String address;
        private String storeNumber;
        private String storeContent;
        private String imgUrl;
        private double longitude;
        private double latitude;
        private String openHour;
        private String closeHour;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private Boolean isStoreLiked;
        private List<MenuListDto> menuList;

        public static GetStoreDto toDto(Store store, Double rateAvg, Boolean isStoreLiked) {

            List<MenuListDto> menuList = new ArrayList<>();
            if(!store.getMenuList().isEmpty())
                store.getMenuList().stream().forEach(menu -> menuList.add(MenuListDto.toDto(menu)));

            return GetStoreDto.builder()
                    .writerId(store.getWriterId())
                    .ownerId(store.getOwnerId())
                    .storeName(store.getStoreName())
                    .firstCategory(store.getFirstCategory())
                    .secondCategory(store.getSecondCategory())
                    .thirdCategory(store.getThirdCategory())
                    .address(store.getAddress())
                    .storeNumber(store.getStoreNumber())
                    .storeContent(store.getStoreContent())
                    .imgUrl(store.getImgUrl())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .hasScreen(store.getHasScreen())
                    .isGroupAvailable(store.getIsGroupAvailable())
                    .storeRate(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .isStoreLiked(isStoreLiked)
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
        private String imgUrl;

        public static MenuListDto toDto(Menu menu) {
            return MenuListDto.builder()
                    .menuId(menu.getMenuId())
                    .menuName(menu.getMenuName())
                    .menuContent(menu.getMenuContent())
                    .price(menu.getPrice())
                    .menuLikeCount(menu.getMenuLikeCount())
                    .imgUrl(menu.getImgUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetAllStoreDto {
        private Long storeId;
        private String storeName;
        private String firstCategory;
        private String secondCategory;
        private String thirdCategory;
        private String address;
        private String imgUrl;
        private String openHour;
        private String closeHour;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;
        private double longitude;
        private double latitude;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;

        public static GetAllStoreDto toDto(Store store, Double rateAvg) {
            return GetAllStoreDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .firstCategory(store.getFirstCategory())
                    .secondCategory(store.getSecondCategory())
                    .thirdCategory(store.getThirdCategory())
                    .address(store.getAddress())
                    .imgUrl(store.getImgUrl())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .hasScreen(store.getHasScreen())
                    .isGroupAvailable(store.getIsGroupAvailable())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .storeRate(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetLikedStoreDto {
        private Long storeId;
        private String storeName;
        private String firstCategory;
        private String secondCategory;
        private String thirdCategory;
        private String address;
        private String imgUrl;
        private String openHour;
        private String closeHour;
        private double longitude;
        private double latitude;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;

        public static GetLikedStoreDto toDto(Store store, Double rateAvg) {
            return GetLikedStoreDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .firstCategory(store.getFirstCategory())
                    .secondCategory(store.getSecondCategory())
                    .thirdCategory(store.getThirdCategory())
                    .address(store.getAddress())
                    .imgUrl(store.getImgUrl())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .storeRate(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .build();
        }
    }

}
