package com.kymokim.spirit.liveReview.dto;

import com.kymokim.spirit.liveReview.entity.LiveReview;
import lombok.Builder;
import lombok.Getter;

public class ResponseLiveReview {

    @Builder
    @Getter
    public static class GetLiveReviewDto {
        private Long liveReviewId;
        private String liveReviewContent;
        private String writerNickName;

        public static GetLiveReviewDto toDto(LiveReview liveReview, String writerNickName) {
            return GetLiveReviewDto.builder()
                    .liveReviewId(liveReview.getLiveReviewId())
                    .liveReviewContent(liveReview.getLiveReviewContent())
                    .writerNickName(writerNickName)
                    .build();
        }
    }
}
