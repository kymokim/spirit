package com.kymokim.spirit.store.dto;

import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;


public class RequestStore {

    @Data
    @Builder
    public static class CreateStoreDto {
        private String storeName;
        private String firstCategory;
        private String secondCategory;
        private String thirdCategory;
        private String address;
        private String storeNumber;
        private String storeContent;
        private double longitude;
        private double latitude;
        private String openHour;
        private String closeHour;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;

        public static Store toEntity(CreateStoreDto createStoreDto, Long writerId) {
            return Store.builder()
                    .writerId(writerId)
                    .storeName(createStoreDto.getStoreName())
                    .firstCategory(createStoreDto.getFirstCategory())
                    .secondCategory(createStoreDto.getSecondCategory())
                    .thirdCategory(createStoreDto.getThirdCategory())
                    .address(createStoreDto.getAddress())
                    .storeNumber(createStoreDto.getStoreNumber())
                    .storeContent(createStoreDto.getStoreContent())
                    .longitude(createStoreDto.getLongitude())
                    .latitude(createStoreDto.getLatitude())
                    .openHour(createStoreDto.getOpenHour())
                    .closeHour(createStoreDto.getCloseHour())
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
        private String firstCategory;
        private String secondCategory;
        private String thirdCategory;
        private String address;
        private String storeNumber;
        private String storeContent;
        private double longitude;
        private double latitude;
        private String openHour;
        private String closeHour;
        private Boolean hasScreen;
        private Boolean isGroupAvailable;

        public static Store toEntity(Store store, UpdateStoreDto updateStoreDto) {
            store.update(updateStoreDto.getStoreName(), updateStoreDto.getFirstCategory(), updateStoreDto.getSecondCategory(), updateStoreDto.getThirdCategory(),
                    updateStoreDto.getAddress(), updateStoreDto.getStoreNumber(), updateStoreDto.getStoreContent(), updateStoreDto.getLongitude(),
                    updateStoreDto.getLatitude(), updateStoreDto.getOpenHour(), updateStoreDto.getCloseHour(), updateStoreDto.getHasScreen(), updateStoreDto.getIsGroupAvailable());
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
