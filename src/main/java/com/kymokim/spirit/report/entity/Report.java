package com.kymokim.spirit.report.entity;

import com.kymokim.spirit.auth.entity.Auth;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDateTime;

@Table
@Entity
@Getter
@Data
@NoArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Enumerated(EnumType.STRING)
    private ReportTarget reportTarget;

    @Column
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private ReportReason reportReason;

    @Column
    private String description;

    @ColumnDefault("'PENDING'")
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus = ReportStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_id", nullable = false)
    private Auth reporter;

    @Builder
    public Report(ReportTarget reportTarget, Long targetId, ReportReason reportReason, String description, Auth reporter){
        this.reportTarget = reportTarget;
        this.targetId = targetId;
        this.reportReason = reportReason;
        this.description = description;
        this.reporter = reporter;
    }

    public void handleReport(ReportStatus reportStatus){
        this.reportStatus = reportStatus;
    }
}
