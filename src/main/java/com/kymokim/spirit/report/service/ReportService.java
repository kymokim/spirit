package com.kymokim.spirit.report.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.report.dto.RequestReport;
import com.kymokim.spirit.report.dto.ResponseReport;
import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.report.exception.ReportErrorCode;
import com.kymokim.spirit.report.repository.ReportRepository;
import com.kymokim.spirit.review.entity.Review;
import com.kymokim.spirit.review.exception.ReviewErrorCode;
import com.kymokim.spirit.review.repository.ReviewRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final AuthRepository authRepository;


    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }

    private Review resolveReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    private Report resolveReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ReportErrorCode.REPORT_NOT_FOUND));
    }

    private Auth resolveUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth user = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Transactional
    public void createReport(RequestReport.CreateReportRqDto createReportRqDto) {
        Auth reporter = resolveUser();
        Report report = createReportRqDto.toEntity(reporter);
        reportRepository.save(report);
    }

    @Transactional
    public Page<ResponseReport.StoreReportListDto> getStoreReports(Pageable pageable, ReportStatus reportStatus) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(
                        ReportTarget.STORE, reportStatus, pageable);
            return reportPage.map(report ->
                    ResponseReport.StoreReportListDto.toDto(
                            report,
                            resolveStore(report.getTargetId())
                    )
            );
        }, 3);
    }

    @Transactional
    public Page<ResponseReport.StoreReportListDto> getPriorityStoreReports(Pageable pageable, ReportStatus reportStatus) {
        List<ReportReason> priorityReasons = List.of(
                ReportReason.INAPPROPRIATE_LANGUAGE,
                ReportReason.INAPPROPRIATE_PHOTO,
                ReportReason.VIOLATION_OF_GUIDELINES
        );
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusAndReportReasonInOrderByReportedAtAsc(
                    ReportTarget.STORE, reportStatus, priorityReasons, pageable);

            return reportPage.map(report ->
                    ResponseReport.StoreReportListDto.toDto(
                            report,
                            resolveStore(report.getTargetId())
                    )
            );
        }, 3);
    }

    @Transactional
    public Page<ResponseReport.ReviewReportListDto> getReviewReports(Pageable pageable, ReportStatus reportStatus) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(
                        ReportTarget.REVIEW, reportStatus, pageable);
            return reportPage.map(report ->
                    ResponseReport.ReviewReportListDto.toDto(
                            report,
                            resolveReview(report.getTargetId())
                    )
            );
        }, 3);
    }

    @Transactional
    public Page<ResponseReport.ReviewReportListDto> getPriorityReviewReports(Pageable pageable, ReportStatus reportStatus) {
        List<ReportReason> priorityReasons = List.of(
                ReportReason.INAPPROPRIATE_LANGUAGE,
                ReportReason.INAPPROPRIATE_PHOTO,
                ReportReason.VIOLATION_OF_GUIDELINES
        );
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusAndReportReasonInOrderByReportedAtAsc(
                    ReportTarget.REVIEW, reportStatus, priorityReasons, pageable);

            return reportPage.map(report ->
                    ResponseReport.ReviewReportListDto.toDto(
                            report,
                            resolveReview(report.getTargetId())
                    )
            );
        }, 3);
    }

    @Transactional
    public void handleReport(ReportStatus reportStatus, Long reportId) {
        Report report = resolveReport(reportId);
        report.handleReport(reportStatus);
    }
}
