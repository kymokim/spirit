package com.kymokim.spirit.review.dto;

import com.kymokim.spirit.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ResponseReview {

    @Builder
    @Getter
    public static class GetReviewDto {
        private Long id;
        private Long writerId;
        private String writerNickname;
        private String content;
        private Double rate;
        private Long storeId;
        private List<String> imgUrlList;

        public static GetReviewDto toDto(Review review) {

            List<String> imgUrlList = new ArrayList<>();
            if(!review.getImgUrlList().isEmpty()) {
                review.getImgUrlList().forEach(reviewImage -> imgUrlList.add(reviewImage.getUrl()));
            }

            return GetReviewDto.builder()
                    .id(review.getId())
                    .writerId(review.getWriterId())
                    .writerNickname(review.getWriterNickname())
                    .content(review.getContent())
                    .rate(review.getRate())
                    .storeId(review.getStore().getId())
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class ReviewListDto {
        private Long id;
        private String writerNickname;
        private String content;
        private Double rate;
        private List<String> imgUrlList;

        public static ReviewListDto toDto(Review review) {

            List<String> imgUrlList = new ArrayList<>();
            if (!review.getImgUrlList().isEmpty()) {
                review.getImgUrlList().forEach(reviewImage -> imgUrlList.add(reviewImage.getUrl()));
            }

            return ReviewListDto.builder()
                    .id(review.getId())
                    .writerNickname(review.getWriterNickname())
                    .content(review.getContent())
                    .rate(review.getRate())
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
        private Long storeId;
        private String storeName;
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
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getName())
                    .imgUrlList(imgUrlList)
                    .build();
        }
    }
}
