package com.example.Fooding.liveReview.dto;

import com.example.Fooding.liveReview.entity.LiveReview;
import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;


public class RequestLiveReview {

    @Data
    @Builder
    public static class CreateLiveReviewDto {
        private String liveReviewContent;
        private Long storeId;

        public static LiveReview toEntity(CreateLiveReviewDto createLiveReviewDto, Store store, Long makerId) {
            return LiveReview.builder()
                    .makerId(makerId)
                    .liveReviewContent(createLiveReviewDto.getLiveReviewContent())
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateLiveReviewDto {
        private Long liveReviewId;
        private String liveReviewContent;

        public static LiveReview toEntity(LiveReview liveReview, UpdateLiveReviewDto updateLiveReviewDto) {
            liveReview.update(updateLiveReviewDto.getLiveReviewContent());
            return liveReview;
        }
    }
}
