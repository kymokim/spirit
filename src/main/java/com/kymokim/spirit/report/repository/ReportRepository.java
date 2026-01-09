package com.kymokim.spirit.report.repository;

import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReportTargetAndTargetIdAndReportStatusAndReportReasonIn(
            ReportTarget reportTarget,
            Long targetId,
            ReportStatus reportStatus,
            List<ReportReason> reasons
    );

    boolean existsByReportTargetAndTargetIdAndReporterIdAndReportStatus(
            ReportTarget reportTarget,
            Long targetId,
            Long reporterId,
            ReportStatus reportStatus
    );

    Page<Report> findAllByReportTargetAndReportStatusOrderByReportedAtAsc(
            ReportTarget reportTarget,
            ReportStatus reportStatus,
            Pageable pageable
    );

    Page<Report> findAllByReportTargetAndReportStatusAndReportReasonInOrderByReportedAtAsc(
            ReportTarget reportTarget,
            ReportStatus reportStatus,
            List<ReportReason> reasons,
            Pageable pageable
    );

    List<Report> findAllByReportTargetAndTargetIdAndReportStatus(
            ReportTarget reportTarget,
            Long targetId,
            ReportStatus reportStatus
    );

    List<Report> findAllByReportTargetAndTargetId(
            ReportTarget reportTarget,
            Long targetId
    );

    Long countByReportTargetAndTargetIdAndReportStatusAndReportReasonIn(
            ReportTarget reportTarget,
            Long targetId,
            ReportStatus reportStatus,
            List<ReportReason> reportReasons
    );
}