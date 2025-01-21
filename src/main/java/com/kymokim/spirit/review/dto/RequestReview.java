package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;


public class RequestReview {

    @Data
    @Builder
    public static class CreateReviewDto {
        private String content;
        private Double rate;
        private Long storeId;

        public static Review toEntity(CreateReviewDto createReviewDto, Store store, Long writerId, String writerNickname) {
            return Review.builder()
                    .writerId(writerId)
                    .writerNickname(writerNickname)
                    .content(createReviewDto.getContent())
                    .rate(createReviewDto.getRate())
                    .store(store)
                    .build();
        }
    }




    @Data
    @Builder
    public static class UpdateReviewDto {
        private Long reviewId;
        private String content;
        private Double rate;

        public static Review toEntity(Review review, UpdateReviewDto updateReviewDto) {
            review.update(updateReviewDto.getContent(), updateReviewDto.getRate());
            return review;
        }
    }
}
