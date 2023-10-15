package com.example.Fooding.store.dto;

import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;

public class RequestStore {

    @Data
    @Builder
    public static class CreateStoreDto {
        private String storeName;
        private String address;
        private Long longitude;
        private Long latitude;
        private Long openHour;
        private Long closeHour;
        private Long storeNumber;
        private String storeContent;

        public static Store toEntity(CreateStoreDto createStoreDto) {
            return Store.builder()
                    .storeName(createStoreDto.getStoreName()) // 수정: storeName 필드로 초기화
                    .address(createStoreDto.getAddress())
                    .longitude(createStoreDto.getLongitude())
                    .latitude(createStoreDto.getLatitude())
                    .openHour(createStoreDto.getOpenHour())
                    .closeHour(createStoreDto.getCloseHour())
                    .storeNumber(createStoreDto.getStoreNumber())
                    .storeContent(createStoreDto.getStoreContent())
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateStoreDto {
        private Long storeId;
        private String storeName;
        private String address;
        private Long longitude;
        private Long latitude;
        private Long openHour;
        private Long closeHour;
        private Long storeNumber;
        private String storeContent;

        public static Store toEntity(Store store, UpdateStoreDto updateStoreDto) {
            store.update(updateStoreDto.getStoreId(), updateStoreDto.getStoreName(), updateStoreDto.getAddress(), updateStoreDto.getLongitude()
                    , updateStoreDto.getLatitude(), updateStoreDto.getOpenHour(), updateStoreDto.getCloseHour(), updateStoreDto.getStoreNumber()
                    , updateStoreDto.getStoreContent());
            return store;
        }
    }

    @Data
    @Builder
    public static class NearListDto{
        private Long longitude;
        private Long latitude;

        public static Store toEntity(Store store, NearListDto nearListDto) {
            store.nearlist(nearListDto.getLongitude(), nearListDto.getLatitude());
            return store;
        }
    }

}
