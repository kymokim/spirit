package com.kymokim.spirit.report.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.store.dto.ResponseStore;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseReport {

    @Getter
    @Builder
    public static class StoreReportListDto {
        private Long id;
        private LocalDateTime reportedAt;
        private Long storeId; //== targetId
        private String storeName;
        private ReportReason reportReason;
        private String description;
        private ReportStatus reportStatus;
        private Long reporterId;
        private String reporterNickname;

        public static StoreReportListDto toDto(Report report, Store store) {
            return StoreReportListDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .storeId(store.getId())
                    .storeName(store.getName())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(report.getReporter().getId())
                    .reporterNickname(report.getReporter().getNickname())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class ReviewReportListDto {
        private Long id;
        private LocalDateTime reportedAt;
        private Long reviewId; //== targetId
        private Long storeId;
        private String storeName;
        private ReportReason reportReason;
        private String description;
        private ReportStatus reportStatus;
        private Long reporterId;
        private String reporterNickname;
        private Long writerId;
        private String writerNickname;

        public static ReviewReportListDto toDto(Report report, Review review) {
            return ReviewReportListDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .reviewId(review.getId())
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getName())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(report.getReporter().getId())
                    .writerId(review.getWriter().getId())
                    .writerNickname(review.getWriter().getNickname())
                    .build();
        }

    }
}
