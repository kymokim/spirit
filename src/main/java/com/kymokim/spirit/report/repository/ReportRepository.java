package com.kymokim.spirit.report.repository;

import com.kymokim.spirit.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}