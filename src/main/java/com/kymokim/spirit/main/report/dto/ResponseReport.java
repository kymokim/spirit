package com.kymokim.spirit.main.report.dto;

import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.service.AuthResolver;
import com.kymokim.spirit.main.report.entity.Report;
import com.kymokim.spirit.main.report.entity.ReportReason;
import com.kymokim.spirit.main.report.entity.ReportStatus;
import com.kymokim.spirit.main.review.entity.Review;
import com.kymokim.spirit.main.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseReport {

    @Getter
    @Builder
    public static class StoreReportListDto {
        private Long id;
        private LocalDateTime reportedAt;
        private Long storeId; //targetId
        private String storeName;
        private ReportReason reportReason;
        private String description;
        private ReportStatus reportStatus;
        private Long reporterId;
        private String reporterNickname;
        private Boolean isCertified;

        public static StoreReportListDto toDto(Report report, Store store) {
            Auth reporter = AuthResolver.resolveUser(report.getReporterId());
            return StoreReportListDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .storeId(store.getId())
                    .storeName(store.getName())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(reporter.getId())
                    .reporterNickname(reporter.getNickname())
                    .isCertified(store.getOwnerId() != null)
                    .build();
        }

    }

    @Getter
    @Builder
    public static class ReportDto {
        private Long id;
        private LocalDateTime reportedAt;
        private ReportReason reportReason;
        private String description;
        private ReportStatus reportStatus;
        private Long reporterId;
        private String reporterNickname;

        public static ReportDto toDto(Report report) {
            Auth reporter = AuthResolver.resolveUser(report.getReporterId());
            return ReportDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(reporter.getId())
                    .reporterNickname(reporter.getNickname())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReviewReportListDto {
        private Long id;
        private LocalDateTime reportedAt;
        private Long reviewId; //targetId
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
            Auth reporter = AuthResolver.resolveUser(report.getReporterId());
            Auth writer = AuthResolver.resolveUser(review.getWriterId());
            return ReviewReportListDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .reviewId(review.getId())
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getName())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(reporter.getId())
                    .reporterNickname(reporter.getNickname())
                    .writerId(writer.getId())
                    .writerNickname(writer.getNickname())
                    .build();
        }

    }


}
