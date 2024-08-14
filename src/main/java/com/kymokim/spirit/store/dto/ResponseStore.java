package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.menu.entity.Menu;
import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResponseStore {

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long writerId;
        private Long ownerId;
        private String storeName;
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String storeNumber;
        private String storeContent;
        private String imgUrl;
        private double longitude;
        private double latitude;
        private LocalTime openHour;
        private LocalTime closeHour;
        private Set<DayOfWeek> closedDays;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private Boolean isStoreLiked;
        private Boolean isStoreOpen;
        private List<MenuListDto> menuList;

        public static GetStoreDto toDto(Store store, Double rateAvg, Boolean isStoreLiked, Boolean isStoreOpen) {

            List<MenuListDto> menuList = new ArrayList<>();
            if(!store.getMenuList().isEmpty())
                store.getMenuList().stream().forEach(menu -> menuList.add(MenuListDto.toDto(menu)));

            return GetStoreDto.builder()
                    .writerId(store.getWriterId())
                    .ownerId(store.getOwnerId())
                    .storeName(store.getStoreName())
                    .categories(store.getCategories())
                    .address(store.getAddress())
                    .addressDetail(store.getAddressDetail())
                    .storeNumber(store.getStoreNumber())
                    .storeContent(store.getStoreContent())
                    .imgUrl(store.getImgUrl())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .closedDays(store.getClosedDays())
                    .hasScreen(store.getHasScreen())
                    .isGroupAvailable(store.getIsGroupAvailable())
                    .storeRate(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .isStoreLiked(isStoreLiked)
                    .isStoreOpen(isStoreOpen)
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
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String imgUrl;
        private LocalTime openHour;
        private LocalTime closeHour;
        private Set<DayOfWeek> closedDays;
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
                    .categories(store.getCategories())
                    .address(store.getAddress())
                    .addressDetail(store.getAddressDetail())
                    .imgUrl(store.getImgUrl())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .closedDays(store.getClosedDays())
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
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String imgUrl;
        private LocalTime openHour;
        private LocalTime closeHour;
        private Set<DayOfWeek> closedDays;
        private double longitude;
        private double latitude;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;

        public static GetLikedStoreDto toDto(Store store, Double rateAvg) {
            return GetLikedStoreDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .categories(store.getCategories())
                    .address(store.getAddress())
                    .addressDetail(store.getAddressDetail())
                    .imgUrl(store.getImgUrl())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .closedDays(store.getClosedDays())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .storeRate(rateAvg)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .build();
        }
    }

}
