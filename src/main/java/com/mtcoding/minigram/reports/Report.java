package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "reports_tb")
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 신고 대상 유형 (POST / STORY)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('POST','STORY')")
    private ReportType type;

    // 대상 엔티티의 PK (post_id / story_id)
    @Column(nullable = false)
    private Integer targetId;

    // 신고자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private User reporter;

    // 신고 사유 코드 (report_reasons.id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "report_reasons_id", nullable = false)
    private ReportReason reason;

    // 처리 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING'")
    private ReportStatus status = ReportStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Report(Integer id, ReportType type, Integer targetId, User reporter, ReportReason reason, ReportStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.targetId = targetId;
        this.reporter = reporter;
        this.reason = reason;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
