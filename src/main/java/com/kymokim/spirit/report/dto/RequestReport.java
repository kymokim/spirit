package com.kymokim.spirit.report.dto;

import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportTarget;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class RequestReport {

    @Data
    @Builder
    public static class CreateReportRqDto {

        //STORE, POST, COMMENT
        @NotNull
        private ReportTarget reportTarget;
        @NotNull
        private Long targetId;
        @NotNull
        private ReportReason reportReason;
        private String description;

        public Report toEntity(Long reporterId) {
            return Report.builder()
                    .reportTarget(this.reportTarget)
                    .targetId(this.targetId)
                    .reportReason(this.reportReason)
                    .description(this.description)
                    .reporterId(reporterId)
                    .build();
        }
    }

    @Data
    @Builder
    public static class ArchiveReportDto{
        @NotEmpty
        private String handleResult;
        private String targetContent;
    }
}
