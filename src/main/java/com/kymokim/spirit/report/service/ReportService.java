package com.kymokim.spirit.report.service;

import com.kymokim.spirit.auth.entity.Role;
import com.kymokim.spirit.auth.exception.AuthErrorCode;
import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.archive.service.ArchiveService;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.comment.entity.Comment;
import com.kymokim.spirit.comment.exception.CommentErrorCode;
import com.kymokim.spirit.comment.repository.CommentRepository;
import com.kymokim.spirit.common.annotation.MainTransactional;
import com.kymokim.spirit.common.exception.CustomException;
import com.kymokim.spirit.common.service.TransactionRetryUtil;
import com.kymokim.spirit.post.entity.Post;
import com.kymokim.spirit.post.exception.PostErrorCode;
import com.kymokim.spirit.post.repository.PostRepository;
import com.kymokim.spirit.report.dto.RequestReport;
import com.kymokim.spirit.report.dto.ResponseReport;
import com.kymokim.spirit.report.entity.Report;
import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportStatus;
import com.kymokim.spirit.report.entity.ReportTarget;
import com.kymokim.spirit.report.exception.ReportErrorCode;
import com.kymokim.spirit.report.repository.ReportRepository;
import com.kymokim.spirit.store.entity.Store;
import com.kymokim.spirit.store.exception.StoreErrorCode;
import com.kymokim.spirit.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@MainTransactional
public class ReportService {
    private final ReportRepository reportRepository;
    private final StoreRepository storeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ArchiveService archiveService;

    private Report resolveReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ReportErrorCode.REPORT_NOT_FOUND));
    }
    private Store resolveStore(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(StoreErrorCode.STORE_NOT_FOUND));
    }
    private Post resolvePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(PostErrorCode.POST_NOT_FOUND));
    }
    private Comment resolveComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(CommentErrorCode.COMMENT_NOT_FOUND));
    }
    //todo preauthorize 정상 적용 후 제거
    private void validateAdminAccess() {
        if(!AuthResolver.resolveUser().getRoles().contains(Role.ADMIN)){
            throw new CustomException(AuthErrorCode.ADMIN_NOT_FOUND);
        }
    }

    public void createReport(RequestReport.CreateReportRqDto createReportRqDto) {
        Report report = createReportRqDto.toEntity(AuthResolver.resolveUserId());
        reportRepository.save(report);
    }

    public void completeReport(Long reportId) {
        validateAdminAccess();
        Report report = resolveReport(reportId);
        report.handleReport(ReportStatus.COMPLETED);
    }

    public void archiveReport(Long reportId, RequestReport.ArchiveReportDto archiveReportDto) {
        validateAdminAccess();
        Report report = resolveReport(reportId);
        if (report.getReportTarget().equals(ReportTarget.POST)) {
            Post reportedPost = resolvePost(report.getTargetId());
            archiveService.archiveReport(report, reportedPost.getContent(), AuthResolver.resolveUser(reportedPost.getHistoryInfo().getCreatorId()), archiveReportDto.getHandleResult());
        } else if (report.getReportTarget().equals(ReportTarget.STORE)) {
            if (Objects.equals(archiveReportDto.getTargetContent(), null) || archiveReportDto.getTargetContent().isEmpty())
                throw new CustomException(ReportErrorCode.REPORT_TARGET_CONTENT_EMPTY);
            Store reportedStore = resolveStore(report.getTargetId());
            Auth owner = AuthResolver.resolveUser(reportedStore.getId());
            archiveService.archiveReport(report, archiveReportDto.getTargetContent(), owner, archiveReportDto.getHandleResult());
        }
        report.handleReport(ReportStatus.ARCHIVED);
    }

    @MainTransactional(readOnly = true)
    public List<ResponseReport.ReportDto> getReportsByTargetId(ReportTarget reportTarget, Long targetId, boolean fetchAllStatus) {
        validateAdminAccess();
        List<Report> reports;
        if (fetchAllStatus) {
            reports = reportRepository.findAllByReportTargetAndTargetId(reportTarget, targetId);
        } else {
            reports = reportRepository.findAllByReportTargetAndTargetIdAndReportStatus(reportTarget, targetId, ReportStatus.PENDING);
        }
        return reports.stream().map(ResponseReport.ReportDto::toDto).toList();
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseReport.StoreReportListDto> getStoreReports(Pageable pageable, ReportStatus reportStatus, boolean applyPriority) {
        validateAdminAccess();
        return TransactionRetryUtil.executeWithRetry(() -> {

            Page<Report> reportPage;
            if (applyPriority) {
                List<ReportReason> priorityReasons = List.of(
                        ReportReason.INAPPROPRIATE_LANGUAGE,
                        ReportReason.INAPPROPRIATE_PHOTO,
                        ReportReason.VIOLATION_OF_GUIDELINES
                );
                reportPage = reportRepository.findAllByReportTargetAndReportStatusAndReportReasonInOrderByReportedAtAsc(ReportTarget.STORE, reportStatus, priorityReasons, pageable);
            } else {
                reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(ReportTarget.STORE, reportStatus, pageable);
            }

            return reportPage.map(report -> ResponseReport.StoreReportListDto.toDto(report, resolveStore(report.getTargetId())));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseReport.PostReportListDto> getPostReports(Pageable pageable, ReportStatus reportStatus) {
        validateAdminAccess();
        return TransactionRetryUtil.executeWithRetry(() -> {

            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(ReportTarget.POST, reportStatus, pageable);

            return reportPage.map(report -> ResponseReport.PostReportListDto.toDto(report, resolvePost(report.getTargetId())));
        }, 3);
    }

    @MainTransactional(readOnly = true)
    public Page<ResponseReport.CommentReportListDto> getCommentReports(Pageable pageable, ReportStatus reportStatus) {
        validateAdminAccess();
        return TransactionRetryUtil.executeWithRetry(() -> {

            Page<Report> reportPage = reportRepository.findAllByReportTargetAndReportStatusOrderByReportedAtAsc(ReportTarget.COMMENT, reportStatus, pageable);

            return reportPage.map(report -> ResponseReport.CommentReportListDto.toDto(report, resolveComment(report.getTargetId())));
        }, 3);
    }
}
