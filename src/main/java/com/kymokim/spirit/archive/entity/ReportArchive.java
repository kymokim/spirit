package com.kymokim.spirit.archive.entity;

import com.kymokim.spirit.report.entity.ReportReason;
import com.kymokim.spirit.report.entity.ReportTarget;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "report_archive")
@Entity
@Getter
@NoArgsConstructor
@Data
public class ReportArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    private ReportTarget reportTarget;

    @Column
    private Long targetId;

    @Column
    private String targetContent;

    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    @Column
    private String reportDescription;

    @Column
    private Long reporterId;

    @Column
    private String reporterNickname;

    @Column
    private Long reportedUserId;

    @Column
    private String reportedUserNickname;

    @Column
    private String handleResult;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Builder
    public ReportArchive(LocalDateTime reportedAt, ReportTarget reportTarget, Long targetId, String targetContent, ReportReason reportReason, String reportDescription,
                         Long reporterId, String reporterNickname, Long reportedUserId, String reportedUserNickname, String handleResult, LocalDateTime expirationDate){
        this.reportedAt = reportedAt;
        this.reportTarget = reportTarget;
        this.targetId = targetId;
        this.targetContent = targetContent;
        this.reportReason = reportReason;
        this.reportDescription = reportDescription;
        this.reporterId = reporterId;
        this.reporterNickname = reporterNickname;
        this.reportedUserId = reportedUserId;
        this.reportedUserNickname = reportedUserNickname;
        this.handleResult = handleResult;
        this.expirationDate = expirationDate;
    }
}