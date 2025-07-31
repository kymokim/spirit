package com.kymokim.spirit.main.review.dto;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.main.review.entity.Review;
import com.kymokim.spirit.main.store.entity.Store;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;


public class RequestReview {

    @Data
    @Builder
    public static class CreateReviewDto {
        @NotEmpty
        private String content;
        @NotEmpty
        private Double rate;
        @NotEmpty
        private LocalDateTime visitedAt;
        @NotEmpty
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
        @NotEmpty
        private Double rate;
        @NotEmpty
        private LocalDateTime visitedAt;

        public Review toEntity(Review review) {
            review.update(this.content, this.rate, this.visitedAt);
            return review;
        }
    }

    @Data
    @Builder
    public static class SetReplyDto {
        @NotEmpty
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
