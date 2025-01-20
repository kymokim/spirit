package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResponseStore {

    @Getter
    @Builder
    public static class GetAllStoreDto {
        private Long storeId;
        private String name;

        public static GetAllStoreDto toDto(Store store){
            return GetAllStoreDto.builder()
                    .storeId(store.getId())
                    .name(store.getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CreateStoreRsDto{
        private Long storeId;
        public static CreateStoreRsDto toDto(Store store){
            return CreateStoreRsDto.builder()
                    .storeId(store.getId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetStoreDto {
        private Long id;
        private String mainImgUrl;
        private String name;
        private String contact;
        private String description;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;
        private CommonStore.LocationDto locationDto;
        private CommonStore.BusinessHoursDto businessHoursDto;
        private Set<Category> categories;
        private Set<CommonStore.MainDrinkDto> mainDrinkDtos;
        private Set<DayOfWeek> closedDays;
        private Double storeRate;
        private Long reviewCount;
        private Long storeLikeCount;
        private Boolean isStoreLiked;
        private Boolean isStoreOpen;
        private List<String> imgUrlList;

        public static GetStoreDto toDto(Store store, Double storeRate, Boolean isStoreLiked, Boolean isStoreOpen) {

            Set<CommonStore.MainDrinkDto> mainDrinkDtos = new HashSet<>();
            if (!store.getMainDrinks().isEmpty()){
                store.getMainDrinks().forEach(mainDrink -> mainDrinkDtos.add(CommonStore.MainDrinkDto.toDto(mainDrink)));
            }

            List<String> imgUrlList = new ArrayList<>();
            if(!store.getImgUrlList().isEmpty()) {
                store.getImgUrlList().forEach(storeImage -> imgUrlList.add(storeImage.getUrl()));
            }

            return GetStoreDto.builder()
                    .id(store.getId())
                    .mainImgUrl(store.getMainImgUrl())
                    .name(store.getName())
                    .contact(store.getContact())
                    .description(store.getDescription())
                    .hasScreen(store.getHasScreen())
                    .isGroupAvailable(store.getIsGroupAvailable())
                    .locationDto(CommonStore.LocationDto.toDto(store.getLocation()))
                    .businessHoursDto(CommonStore.BusinessHoursDto.toDto(store.getBusinessHours()))
                    .categories(store.getCategories())
                    .mainDrinkDtos(mainDrinkDtos)
                    .closedDays(store.getClosedDays())
                    .storeRate(storeRate)
                    .reviewCount(store.getReviewCount())
                    .storeLikeCount(store.getStoreLikeCount())
                    .isStoreLiked(isStoreLiked)
                    .isStoreOpen(isStoreOpen)
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

//    @Getter
//    @Builder
//    public static class GetAllStoreDto {
//        private Long storeId;
//        private String storeName;
//        private Set<Category> categories;
//        private String address;
//        private String addressDetail;
//        private String mainImgUrl;
//        private LocalTime openHour;
//        private LocalTime closeHour;
//        private Set<DayOfWeek> closedDays;
//        private Boolean hasScreen;
//        private Boolean isGroupAvailable;
//        private double longitude;
//        private double latitude;
//        private Double storeRate;
//        private Long reviewCount;
//        private Long storeLikeCount;
//
//        public static GetAllStoreDto toDto(Store store, Double rateAvg) {
//            return GetAllStoreDto.builder()
//                    .storeId(store.getStoreId())
//                    .storeName(store.getStoreName())
//                    .categories(store.getCategories())
//                    .address(store.getAddress())
//                    .addressDetail(store.getAddressDetail())
//                    .mainImgUrl(store.getMainImgUrl())
//                    .openHour(store.getOpenHour())
//                    .closeHour(store.getCloseHour())
//                    .closedDays(store.getClosedDays())
//                    .hasScreen(store.getHasScreen())
//                    .isGroupAvailable(store.getIsGroupAvailable())
//                    .longitude(store.getLongitude())
//                    .latitude(store.getLatitude())
//                    .storeRate(rateAvg)
//                    .reviewCount(store.getReviewCount())
//                    .storeLikeCount(store.getStoreLikeCount())
//                    .build();
//        }
//    }
//
//    @Getter
//    @Builder
//    public static class GetByDistanceDto {
//        private Long storeId;
//        private String storeName;
//        private Set<Category> categories;
//        private String address;
//        private String addressDetail;
//        private String mainImgUrl;
//        private LocalTime openHour;
//        private LocalTime closeHour;
//        private Set<DayOfWeek> closedDays;
//        private Boolean hasScreen;
//        private Boolean isGroupAvailable;
//        private double longitude;
//        private double latitude;
//        private Double storeRate;
//        private Long reviewCount;
//        private Long storeLikeCount;
//        private Double distance;
//
//        public static GetByDistanceDto toDto(Store store, Double rateAvg, Double distance) {
//            return GetByDistanceDto.builder()
//                    .storeId(store.getStoreId())
//                    .storeName(store.getStoreName())
//                    .categories(store.getCategories())
//                    .address(store.getAddress())
//                    .addressDetail(store.getAddressDetail())
//                    .mainImgUrl(store.getMainImgUrl())
//                    .openHour(store.getOpenHour())
//                    .closeHour(store.getCloseHour())
//                    .closedDays(store.getClosedDays())
//                    .hasScreen(store.getHasScreen())
//                    .isGroupAvailable(store.getIsGroupAvailable())
//                    .longitude(store.getLongitude())
//                    .latitude(store.getLatitude())
//                    .storeRate(rateAvg)
//                    .reviewCount(store.getReviewCount())
//                    .storeLikeCount(store.getStoreLikeCount())
//                    .distance(distance)
//                    .build();
//        }
//    }
//
//    @Getter
//    @Builder
//    public static class GetLikedStoreDto {
//        private Long storeId;
//        private String storeName;
//        private Set<Category> categories;
//        private String address;
//        private String addressDetail;
//        private String mainImgUrl;
//        private LocalTime openHour;
//        private LocalTime closeHour;
//        private Set<DayOfWeek> closedDays;
//        private double longitude;
//        private double latitude;
//        private Double storeRate;
//        private Long reviewCount;
//        private Long storeLikeCount;
//
//        public static GetLikedStoreDto toDto(Store store, Double rateAvg) {
//            return GetLikedStoreDto.builder()
//                    .storeId(store.getStoreId())
//                    .storeName(store.getStoreName())
//                    .categories(store.getCategories())
//                    .address(store.getAddress())
//                    .addressDetail(store.getAddressDetail())
//                    .mainImgUrl(store.getMainImgUrl())
//                    .openHour(store.getOpenHour())
//                    .closeHour(store.getCloseHour())
//                    .closedDays(store.getClosedDays())
//                    .longitude(store.getLongitude())
//                    .latitude(store.getLatitude())
//                    .storeRate(rateAvg)
//                    .reviewCount(store.getReviewCount())
//                    .storeLikeCount(store.getStoreLikeCount())
//                    .build();
//        }
//    }
}
