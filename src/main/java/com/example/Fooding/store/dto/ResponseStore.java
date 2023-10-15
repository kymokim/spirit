package com.example.Fooding.store.dto;

import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseStore {

    // 가게 등록, 수정, 삭제 response
    @Getter
    @Builder
    public static class GetStoreDto {
        private Long userId;
        private LocalDateTime reviewDate;

        public static GetStoreDto toDto(Store store) {

            return GetStoreDto.builder()
                    .userId(store.getUserId())
                    .reviewDate(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetReadStoreDto {
        private Long userId;
        private Long ownerId;
        private String storeName;
        private String category;
        private Long totalRate;
        private String address;
        private Long longitude;
        private Long latitude;
        private Long openHour;
        private Long closeHour;
        private Long storeNumber;
        private String storeContent;
        private Long storeLikeCount;

        public static GetReadStoreDto toDto(Store store) {
            return GetReadStoreDto.builder()
                    .userId(store.getUserId())
                    .ownerId(store.getOwnerId())
                    .storeName(store.getStoreName())
                    .category(store.getCategory())
                    .totalRate(store.getTotalRate())
                    .address(store.getAddress())
                    .longitude(store.getLongitude())
                    .latitude(store.getLatitude())
                    .openHour(store.getOpenHour())
                    .closeHour(store.getCloseHour())
                    .storeNumber(store.getStoreNumber())
                    .storeContent(store.getStoreContent())
                    .storeLikeCount(store.getStoreLikeCount())
                    .build();
        }
    }


    // 내주변, 최근 방문, 카테고리 별 가게 리스트 조회 API
    @Getter
    @Builder
    public static class GetNearListDto {
        private Long storeId;
        private String storeName;

        public static GetNearListDto toDto(Store store) {
            return GetNearListDto.builder()
                    .storeId(store.getStoreId())
                    .storeName(store.getStoreName())
                    .build();
        }
    }

}
