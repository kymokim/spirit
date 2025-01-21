package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

public class ResponseReview {

    @Builder
    @Getter
    public static class GetReviewDto {
        private Long reviewId;
        private String writerNickname;
        private String content;
        private Double rate;

        public static GetReviewDto toDto(Review review) {
            return GetReviewDto.builder()
                    .reviewId(review.getId())
                    .writerNickname(review.getWriterNickname())
                    .content(review.getContent())
                    .rate(review.getRate())
                    .build();
        }
    }
}
