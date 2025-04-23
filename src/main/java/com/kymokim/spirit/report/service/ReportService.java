package com.kymokim.spirit.report.service;

import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.report.dto.RequestReport;
import com.kymokim.spirit.report.dto.ResponseReport;
import com.kymokim.spirit.report.entity.Report;
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
                .orElseThrow(() -> new CustomException(ReportErrorCode.REVIEW_NOT_FOUND));
    }

    private Auth resolveUser() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        Auth user = authRepository.findById(userId)
                .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
        return user;
    }

    @Transactional
    public ResponseReport.CreateReportRsDto createReport(RequestReport.CreateReportRqDto createReportRqDto) {
        Auth reporter = resolveUser();
        Report report = createReportRqDto.toEntity(reporter);
        reportRepository.save(report);

        return ResponseReport.CreateReportRsDto.toDto(report);

    }

    @Transactional
    public Page<ResponseReport.StoreReportListDto> getStoreReports(Pageable pageable, ReportStatus reportStatus) {
        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage;
            if (reportStatus != null) {
                reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(
                        ReportTarget.STORE, reportStatus, pageable);
            } else {
                reportPage = reportRepository.findAllByReportTargetOrderByReportedAtAsc(ReportTarget.STORE, pageable);
            }
            return reportPage.map(report ->
                    ResponseReport.StoreReportListDto.toDto(
                            report,
                            resolveStore(report.getTargetId()), // 오타 수정
                            report.getReporter()
                    )
            );
        }, 3);
    }

    @Transactional
    public Page<ResponseReport.ReviewReportListDto> getReviewReports(Pageable pageable, ReportStatus reportStatus) {

        return TransactionRetryUtil.executeWithRetry(() -> {
            Page<Report> reportPage;
            if (reportStatus != null) {
                reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(
                        ReportTarget.REVIEW, reportStatus, pageable);
            } else {
                reportPage = reportRepository.findAllByReportTargetOrderByReportedAtAsc(ReportTarget.REVIEW, pageable);
            }

            return reportPage.map(report ->
                    ResponseReport.ReviewReportListDto.toDto(
                            report,
                            resolveReview(report.getTargetId()),
                            resolveReview(report.getTargetId()).getStore(),
                            report.getReporter()
                    )
            );
        }, 3);
    }

    @Transactional
    public void updateReportStatus(ReportStatus reportStatus, Long reportId) {
        Report report = resolveReport(reportId); // 예외 처리 포함된 리졸버
        report.handleReport(reportStatus);
    }

}
