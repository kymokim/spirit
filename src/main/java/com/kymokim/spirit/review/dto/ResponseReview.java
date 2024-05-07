package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

public class ResponseReview {

    @Builder
    @Getter
    public static class GetReviewDto {
        private Long reviewId;
        private String writerNickName;
        private String reviewContent;
        private Double rate;

        public static GetReviewDto toDto(Review review) {
            return GetReviewDto.builder()
                    .reviewId(review.getReviewId())
                    .writerNickName(review.getWriterNickName())
                    .reviewContent(review.getReviewContent())
                    .rate(review.getRate())
                    .build();
        }
    }
}
