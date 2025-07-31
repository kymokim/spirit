package com.kymokim.spirit.main.report.service;

import com.kymokim.spirit.auth.auth.service.AuthResolver;
import com.kymokim.spirit.main.archive.service.ArchiveService;
import com.kymokim.spirit.auth.auth.entity.Auth;
import com.kymokim.spirit.auth.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.auth.repository.AuthRepository;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.main.report.dto.RequestReport;
import com.kymokim.spirit.main.report.dto.ResponseReport;
import com.kymokim.spirit.main.report.entity.Report;
import com.kymokim.spirit.main.report.entity.ReportReason;
import com.kymokim.spirit.main.report.entity.ReportStatus;
import com.kymokim.spirit.main.report.entity.ReportTarget;
import com.kymokim.spirit.main.report.exception.ReportErrorCode;
import com.kymokim.spirit.main.report.repository.ReportRepository;
import com.kymokim.spirit.main.review.entity.Review;
import com.kymokim.spirit.main.review.exception.ReviewErrorCode;
import com.kymokim.spirit.main.review.repository.ReviewRepository;
import com.kymokim.spirit.main.store.entity.Store;
import com.kymokim.spirit.main.store.exception.StoreErrorCode;
import com.kymokim.spirit.main.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;
    private final AuthRepository authRepository;
    private final ArchiveService archiveService;

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

    @Transactional
    public void createReport(RequestReport.CreateReportRqDto createReportRqDto) {
        Report report = createReportRqDto.toEntity(AuthResolver.resolveUserId());
        reportRepository.save(report);
    }

    @Transactional(readOnly = true)
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


    @Transactional(readOnly = true)
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


    @Transactional(readOnly = true)
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


    @Transactional(readOnly = true)
    public List<ResponseReport.ReportDto> getReportsByTargetId(ReportTarget reportTarget, Long targetId) {
        List<Report> reports = reportRepository.findAllByReportTargetAndTargetIdAndReportStatus(reportTarget, targetId, ReportStatus.PENDING);

        List<ResponseReport.ReportDto> reportList = new ArrayList<>();
        reports.forEach(report -> reportList.add(ResponseReport.ReportDto.toDto(report)));

        return reportList;

    }

    @Transactional
    public void completeReport(Long reportId) {
        Report report = resolveReport(reportId);
        report.handleReport(ReportStatus.COMPLETED);
    }

    @Transactional
    public void archiveReport(Long reportId, RequestReport.ArchiveReportDto archiveReportDto){
        Report report = resolveReport(reportId);
        if (report.getReportTarget().equals(ReportTarget.REVIEW)){
            Review reportedReview = resolveReview(report.getTargetId());
            archiveService.archiveReport(report, reportedReview.getContent(), AuthResolver.resolveUser(reportedReview.getWriterId()), archiveReportDto.getHandleResult());
        }
        else if (report.getReportTarget().equals(ReportTarget.STORE)){
            if (Objects.equals(archiveReportDto.getTargetContent(), null) || archiveReportDto.getTargetContent().isEmpty())
                throw new CustomException(ReportErrorCode.REPORT_TARGET_CONTENT_EMPTY);
            Store reportedStore = resolveStore(report.getTargetId());
            Auth owner = authRepository.findById(reportedStore.getId())
                    .orElseThrow(() -> new CustomException(AuthErrorCode.USER_NOT_FOUND));
            archiveService.archiveReport(report, archiveReportDto.getTargetContent(), owner, archiveReportDto.getHandleResult());
        }
        report.handleReport(ReportStatus.ARCHIVED);
    }
}
