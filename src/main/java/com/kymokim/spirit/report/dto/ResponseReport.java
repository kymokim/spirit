package com.kymokim.spirit.report.dto;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ResponseReport {

    @Getter
    @Builder
    public static class GetReportsByStoreManagerDto {
        private Long id;
        private LocalDateTime reportedAt;
        private ReportReason reportReason;
        private String description;

        public static GetReportsByStoreManagerDto toDto(Report report) {
            return GetReportsByStoreManagerDto.builder()
                    .id(report.getId())
                    .reportedAt(report.getReportedAt())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StoreReportListDto {
        private ReportDto report;
        private String storeName;
        private Boolean isCertified;

        public static StoreReportListDto toDto(Report report, Store store) {
            return StoreReportListDto.builder()
                    .report(ReportDto.toDto(report))
                    .storeName(store.getName())
                    .isCertified(store.getOwnerId() != null)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PostReportListDto {
        private ReportDto report;
        private String writerNickname;
        private Long storeId;
        private String storeName;
        private String place;

        public static PostReportListDto toDto(Report report, Post post) {
            Auth writer = AuthResolver.resolveUser(post.getHistoryInfo().getCreatorId());
            return PostReportListDto.builder()
                    .report(ReportDto.toDto(report))
                    .writerNickname(writer.getNickname())
                    .storeId(post.getStore() == null ? null : post.getStore().getId())
                    .storeName(post.getStore() == null ? null : post.getStore().getName())
                    .place(post.getPlace() == null ? null : post.getPlace())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class CommentReportListDto {
        private ReportDto report;
        private String writerNickname;
        private String content;
        private Long postId;
        private Long rootCommentId;

        public static CommentReportListDto toDto(Report report, Comment comment) {
            Auth writer = AuthResolver.resolveUser(comment.getHistoryInfo().getCreatorId());
            return CommentReportListDto.builder()
                    .report(ReportDto.toDto(report))
                    .writerNickname(writer.getNickname())
                    .content(comment.getContent())
                    .postId(comment.getPost().getId())
                    .rootCommentId(comment.getRootComment() == null ? null : comment.getRootComment().getId())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ReportDto {
        private Long id;
        private LocalDateTime reportedAt;
        private ReportTarget reportTarget;
        private Long targetId;
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
                    .reportTarget(report.getReportTarget())
                    .targetId(report.getTargetId())
                    .reportReason(report.getReportReason())
                    .description(report.getDescription())
                    .reportStatus(report.getReportStatus())
                    .reporterId(reporter.getId())
                    .reporterNickname(reporter.getNickname())
                    .build();
        }
    }
}
