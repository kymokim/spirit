package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.store.entity.Category;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;


public class RequestStore {

    @Data
    @Builder
    public static class CreateStoreDto {
        private String storeName;
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String storeNumber;
        private String storeContent;
        private double longitude;
        private double latitude;
        private LocalTime openHour;
        private LocalTime closeHour;
        private Set<DayOfWeek> closedDays;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;

        public static Store toEntity(CreateStoreDto createStoreDto, Long writerId) {
            return Store.builder()
                    .writerId(writerId)
                    .storeName(createStoreDto.getStoreName())
                    .categories(createStoreDto.getCategories())
                    .address(createStoreDto.getAddress())
                    .addressDetail(createStoreDto.getAddressDetail())
                    .storeNumber(createStoreDto.getStoreNumber())
                    .storeContent(createStoreDto.getStoreContent())
                    .longitude(createStoreDto.getLongitude())
                    .latitude(createStoreDto.getLatitude())
                    .openHour(createStoreDto.getOpenHour())
                    .closeHour(createStoreDto.getCloseHour())
                    .closedDays(createStoreDto.getClosedDays())
                    .hasScreen(createStoreDto.getHasScreen())
                    .isGroupAvailable(createStoreDto.getIsGroupAvailable())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateStoreDto {
        private Long storeId;
        private String storeName;
        private Set<Category> categories;
        private String address;
        private String addressDetail;
        private String storeNumber;
        private String storeContent;
        private double longitude;
        private double latitude;
        private LocalTime openHour;
        private LocalTime closeHour;
        private Set<DayOfWeek> closedDays;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;

        public static Store toEntity(Store store, UpdateStoreDto updateStoreDto) {
            store.update(updateStoreDto.getStoreName(), updateStoreDto.getCategories(),
                    updateStoreDto.getAddress(), updateStoreDto.getAddressDetail(), updateStoreDto.getStoreNumber(), updateStoreDto.getStoreContent(), updateStoreDto.getLongitude(),
                    updateStoreDto.getLatitude(), updateStoreDto.getOpenHour(), updateStoreDto.getCloseHour(), updateStoreDto.getClosedDays(), updateStoreDto.getHasScreen(), updateStoreDto.getIsGroupAvailable());
            return store;
        }
    }

    @Data
    @Builder
    public static class UploadImgDto{
        private Long storeId;
        private Long decId;
    }
}
