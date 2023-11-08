package com.example.Fooding.store.dto;

import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

public class RequestStore {

    @Data
    @Builder
    public static class CreateStoreDto {
        private String storeName;
        private String category;
        private String address;
        private String storeNumber;
        private String storeContent;
        private String longitude;
        private String latitude;
        private Long openHour;
        private Long closeHour;

        public static Store toEntity(CreateStoreDto createStoreDto, Long makerId) {
            return Store.builder()
                    .makerId(makerId)
                    .storeName(createStoreDto.getStoreName())
                    .category(createStoreDto.getCategory())
                    .address(createStoreDto.getAddress())
                    .storeNumber(createStoreDto.getStoreNumber())
                    .storeContent(createStoreDto.getStoreContent())
                    .longitude(createStoreDto.getLongitude())
                    .latitude(createStoreDto.getLatitude())
                    .openHour(createStoreDto.getOpenHour())
                    .closeHour(createStoreDto.getCloseHour())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateStoreDto {
        private Long storeId;
        private String storeName;
        private String category;
        private String address;
        private String storeNumber;
        private String storeContent;
        private String longitude;
        private String latitude;
        private Long openHour;
        private Long closeHour;

        public static Store toEntity(Store store, UpdateStoreDto updateStoreDto) {
            store.update(updateStoreDto.getStoreName(), updateStoreDto.getCategory(), updateStoreDto.getAddress(),
                    updateStoreDto.getStoreNumber(), updateStoreDto.getStoreContent(), updateStoreDto.getLongitude(),
                    updateStoreDto.getLatitude(), updateStoreDto.getOpenHour(), updateStoreDto.getCloseHour());
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
