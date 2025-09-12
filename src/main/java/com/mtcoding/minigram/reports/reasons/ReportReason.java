package com.mtcoding.minigram.reports.reasons;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "report_reasons_tb")
@Entity
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 시스템 코드 (예: "SPAM_SCAM")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private ReportReasonCode code;

    // 사용자에게 보여줄 라벨 (예: "스팸, 사기 또는 스팸")
    @Column(nullable = false)
    private String label;

}
