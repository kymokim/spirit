package com.example.Fooding.liveReview.dto;

import com.example.Fooding.liveReview.entity.LiveReview;
import lombok.Builder;
import lombok.Getter;

public class ResponseLiveReview {

    @Builder
    @Getter
    public static class GetLiveReviewDto {
        private Long liveReviewId;
        private String liveReviewContent;

        public static GetLiveReviewDto toDto(LiveReview liveReview) {
            return GetLiveReviewDto.builder()
                    .liveReviewId(liveReview.getLiveReviewId())
                    .liveReviewContent(liveReview.getLiveReviewContent())
                    .build();
        }
    }
}
