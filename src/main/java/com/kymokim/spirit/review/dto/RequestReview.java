package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


public class RequestReview {

    @Data
    @Builder
    public static class CreateReviewDto {
        @NotEmpty
        private String content;
        @NotNull
        private Double rate;
        @NotNull
        private LocalDateTime visitedAt;
        @NotNull
        private Long storeId;

        public Review toEntity(Store store, Long writerId) {
            return Review.builder()
                    .content(this.content)
                    .rate(this.rate)
                    .visitedAt(this.visitedAt)
                    .writerId(writerId)
                    .store(store)
                    .build();
        }
    }

    @Data
    @Builder
    public static class UpdateReviewDto {
        @NotEmpty
        private String content;
        @NotNull
        private Double rate;
        @NotNull
        private LocalDateTime visitedAt;

        public Review toEntity(Review review) {
            review.update(this.content, this.rate, this.visitedAt);
            return review;
        }
    }

    @Data
    @Builder
    public static class SetReplyDto {
        @NotNull
        private Long reviewId;
        @NotEmpty
        private String reply;
    }

    @Data
    @Builder
    public static class DeleteImageDto {
        @NotEmpty
        private List<String> imgUrlList;
    }
}
