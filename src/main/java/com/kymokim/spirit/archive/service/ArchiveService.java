package com.kymokim.spirit.archive.service;

import com.kymokim.spirit.auth.service.AuthResolver;
import com.kymokim.spirit.archive.entity.ArchiveType;
import com.kymokim.spirit.archive.entity.ReportArchive;
import com.kymokim.spirit.archive.entity.UserArchive;
import com.kymokim.spirit.archive.repository.ReportArchiveRepository;
import com.kymokim.spirit.archive.repository.UserArchiveRepository;
import com.kymokim.spirit.auth.entity.Auth;
import com.kymokim.spirit.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final UserArchiveRepository userArchiveRepository;
    private final ReportArchiveRepository reportArchiveRepository;

    @Transactional
    public void archiveUser(Long originalId, String encryptedCi, ArchiveType type){
        UserArchive userArchive = UserArchive.builder()
                .originalId(originalId)
                .ci(encryptedCi)
                .type(type)
                .expirationDate(LocalDateTime.now().plusMonths(6))
                .build();
        userArchiveRepository.save(userArchive);
    }

    @Transactional
    public void archiveReport(Report report, String targetContent, Auth reportedUser, String handleResult){
        Auth reporter = AuthResolver.resolveUser(report.getReporterId());
        ReportArchive reportArchive = ReportArchive.builder()
                .reportedAt(report.getReportedAt())
                .reportTarget(report.getReportTarget())
                .targetId(report.getTargetId())
                .targetContent(targetContent)
                .reportReason(report.getReportReason())
                .reportDescription(report.getDescription())
                .reporterId(reporter.getId())
                .reporterNickname(reporter.getNickname())
                .reportedUserId(reportedUser.getId())
                .reportedUserNickname(reportedUser.getNickname())
                .handleResult(handleResult)
                .expirationDate(LocalDateTime.now().plusYears(3))
                .build();
        reportArchiveRepository.save(reportArchive);
    }
}
