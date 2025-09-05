package com.mtcoding.minigram.reports;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

public class ReportResponse {

    @Data
    @Builder
    public static class SaveResultDTO {
        private Integer reportId;
        private ReportStatus status;

        public static SaveResultDTO from(Report report) {
            return SaveResultDTO.builder()
                    .reportId(report.getId())
                    .status(report.getStatus())
                    .build();
        }
    }

    @Data
    @Builder
    public static class AdminViewDTO {
        private Integer reportId;
        private ReportType type;
        private Integer targetId;
        private String targetContent;
        private String reporterUsername;
        private String reason;
        private ReportStatus status;
        private LocalDateTime createdAt;

        public static AdminViewDTO from(Report report, String targetContent) {
            return AdminViewDTO.builder()
                    .reportId(report.getId())
                    .type(report.getType())
                    .targetId(report.getTargetId())
                    .targetContent(targetContent)
                    .reporterUsername(report.getReporter().getUsername())
                    .reason(report.getReason().getReasonText())
                    .status(report.getStatus())
                    .createdAt(report.getCreatedAt())
                    .build();
        }
    }