package com.example.Fooding.review.dto;

import com.example.Fooding.review.entity.Review;
import com.example.Fooding.store.entity.Store;
import lombok.Builder;
import lombok.Data;


public class RequestReview {

    @Data
    @Builder
    public static class CreateReviewDto {
        private String reviewContent;
        private Long rate;
        private Long storeId;

        public static Review toEntity(CreateReviewDto createReviewDto, Store store, Long makerId) {
            return Review.builder()
                    .makerId(makerId)
                    .reviewContent(createReviewDto.getReviewContent())
                    .rate(createReviewDto.getRate())
                    .store(store)
                    .build();
        }
    }




    @Data
    @Builder
    public static class UpdateReviewDto {
        private Long reviewId;
        private String reviewContent;
        private Long rate;

        public static Review toEntity(Review review, UpdateReviewDto updateReviewDto) {
            review.update(updateReviewDto.getReviewContent(), updateReviewDto.getRate());
            return review;
        }
    }
}
