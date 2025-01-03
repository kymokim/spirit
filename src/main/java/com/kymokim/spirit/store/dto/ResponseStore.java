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
        private String mainImgUrl;
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
        private Double distance;
        private List<String> imgUrlList;

        public static GetStoreDto toDto(Store store, Double rateAvg, Boolean isStoreLiked, Boolean isStoreOpen, Double distance) {

            List<String> imgUrlList = new ArrayList<>();
            if(!store.getImgUrlList().isEmpty())
                store.getImgUrlList().stream().forEach(storeImage -> imgUrlList.add(storeImage.getUrl()));

            return GetStoreDto.builder()
                    .writerId(store.getWriterId())
                    .ownerId(store.getOwnerId())
                    .storeName(store.getStoreName())
                    .categories(store.getCategories())
                    .address(store.getAddress())
                    .addressDetail(store.getAddressDetail())
                    .storeNumber(store.getStoreNumber())
                    .storeContent(store.getStoreContent())
                    .mainImgUrl(store.getMainImgUrl())
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
                    .distance(distance)
                    .imgUrlList(imgUrlList)
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
        private String mainImgUrl;
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
                    .mainImgUrl(store.getMainImgUrl())
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
    public static class GetByDistanceDto {
        private Long storeId;
        private String storeName;
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String mainImgUrl;
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
        private Double distance;

        public static GetByDistanceDto toDto(Store store, Double rateAvg, Double distance) {
            return GetByDistanceDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .categories(store.getCategories())
                    .address(store.getAddress())
                    .addressDetail(store.getAddressDetail())
                    .mainImgUrl(store.getMainImgUrl())
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
                    .distance(distance)
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
        private String mainImgUrl;
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
                    .mainImgUrl(store.getMainImgUrl())
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
