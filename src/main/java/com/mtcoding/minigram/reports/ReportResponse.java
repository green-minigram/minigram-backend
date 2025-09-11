package com.mtcoding.minigram.reports;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class ReportResponse {

    @Data
    public static class DTO {
        private Integer reportId;
        private ReportType reportType;
        private Integer targetId;
        private Integer userId;
        private Integer reasonId;
        private ReportStatus status;

        public DTO(Report report) {
            this.reportId = report.getId();
            this.reportType = report.getType();
            this.targetId = report.getTargetId();
            this.userId = report.getReporter().getId();
            this.reasonId = report.getReason().getId();
            this.status = report.getStatus();
        }
    }

    @Data
    @Builder
    public static class AdminViewDTO {
        private Integer reportId;
        private ReportType type;
        private Integer targetId;
        private String targetContent;         // 필요 없으면 제거 가능
        private String reporterUsername;
        private String reasonLabel;           // label 사용
        private ReportStatus status;
        private LocalDateTime createdAt;

        public static AdminViewDTO from(Report report) {
            return AdminViewDTO.builder()
                    .reportId(report.getId())
                    .type(report.getType())
                    .targetId(report.getTargetId())
                    .reporterUsername(report.getReporter().getUsername())
                    .reasonLabel(report.getReason().getLabel())
                    .status(report.getStatus())
                    .createdAt(report.getCreatedAt())
                    .build();
        }
    }
}