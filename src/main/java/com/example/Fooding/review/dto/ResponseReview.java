package com.example.Fooding.review.dto;

import com.example.Fooding.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

public class ResponseReview {

    @Builder
    @Getter
    public static class GetReviewDto {
        private Long reviewId;
        private String reviewContent;
        private Double rate;

        public static GetReviewDto toDto(Review review) {
            return GetReviewDto.builder()
                    .reviewId(review.getReviewId())
                    .reviewContent(review.getReviewContent())
                    .rate(review.getRate())
                    .build();
        }
    }
}
