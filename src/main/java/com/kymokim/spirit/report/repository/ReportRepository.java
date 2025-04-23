package com.kymokim.spirit.report.repository;

import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findAllByReportTargetOrderByReportedAtAsc(ReportTarget reportTarget, Pageable pageable);
    Page<Report> findAllByReportTargetAndReportStatusOrderByReportedAtAsc(ReportTarget reportTarget, ReportStatus reportStatus, Pageable pageable);
}