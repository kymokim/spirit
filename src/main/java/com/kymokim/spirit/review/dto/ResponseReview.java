package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.entity.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ResponseReview {

    @Builder
    @Getter
    public static class GetReviewDto {
        private Long id;
        private Long writerId;
        private String writerNickname;
        private String writerImgUrl;
        private String content;
        private Double rate;
        private LocalDateTime visitedAt;
        private Long storeId;
        private String reply;
        private LocalDateTime repliedAt;
        private List<String> imgUrlList;

        public static GetReviewDto toDto(Review review) {

            List<String> imgUrlList = new ArrayList<>();
            if(!review.getImgUrlList().isEmpty()) {
                review.getImgUrlList().forEach(reviewImage -> imgUrlList.add(reviewImage.getUrl()));
            }

            return GetReviewDto.builder()
                    .id(review.getId())
                    .writerId(review.getWriter().getId())
                    .writerNickname(review.getWriter().getNickname())
                    .writerImgUrl(review.getWriter().getImgUrl())
                    .content(review.getContent())
                    .rate(review.getRate())
                    .visitedAt(review.getVisitedAt())
                    .storeId(review.getStore().getId())
                    .reply(review.getReply())
                    .repliedAt(review.getRepliedAt())
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ReviewListDto {
        private Long id;
        private String writerNickname;
        private String writerImgUrl;
        private String content;
        private Double rate;
        private LocalDateTime visitedAt;
        private LocalDateTime createdAt;
        private Boolean isWriter;
        private String reply;
        private LocalDateTime repliedAt;
        private List<String> imgUrlList;

        public static ReviewListDto toDto(Review review, Boolean isWriter) {

            List<String> imgUrlList = new ArrayList<>();
            if (!review.getImgUrlList().isEmpty()) {
                review.getImgUrlList().forEach(reviewImage -> imgUrlList.add(reviewImage.getUrl()));
            }

            return ReviewListDto.builder()
                    .id(review.getId())
                    .writerNickname(review.getWriter().getNickname())
                    .writerImgUrl(review.getWriter().getImgUrl())
                    .content(review.getContent())
                    .rate(review.getRate())
                    .visitedAt(review.getVisitedAt())
                    .createdAt(review.getHistoryInfo().getCreatedAt())
                    .isWriter(isWriter)
                    .reply(review.getReply())
                    .repliedAt(review.getRepliedAt())
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class GetRecentReviewDto {
        private Long id;
        private String content;
        private Double rate;
        private LocalDateTime visitedAt;
        private LocalDateTime createdAt;
        private Long storeId;
        private String storeName;
        private String reply;
        private LocalDateTime repliedAt;
        private Set<Category> categories;
        private List<String> imgUrlList;

        public static GetRecentReviewDto toDto(Review review) {

            List<String> imgUrlList = new ArrayList<>();
            if (!review.getImgUrlList().isEmpty()) {
                review.getImgUrlList().forEach(reviewImage -> imgUrlList.add(reviewImage.getUrl()));
            }

            return GetRecentReviewDto.builder()
                    .id(review.getId())
                    .content(review.getContent())
                    .rate(review.getRate())
                    .visitedAt(review.getVisitedAt())
                    .createdAt(review.getHistoryInfo().getCreatedAt())
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getName())
                    .categories(review.getStore().getCategories())
                    .reply(review.getReply())
                    .repliedAt(review.getRepliedAt())
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }
}
