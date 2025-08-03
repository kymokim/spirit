package com.kymokim.spirit.report.dto;

import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportTarget;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

public class RequestReport {

    @Data
    @Builder
    public static class CreateReportRqDto {

        @NotEmpty
        private ReportTarget reportTarget;
        //REVIEW, STORE
        @NotEmpty
        private Long targetId;
        @NotEmpty
        private ReportReason reportReason;
        @NotEmpty
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
