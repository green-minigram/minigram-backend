package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.reports.reasons.ReportReasonCode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ReportResponse {

    @Data
    public static class ReasonListDTO {
        private List<ReasonItemDTO> reasonList;

        public ReasonListDTO(List<ReasonItemDTO> reasonList) {
            this.reasonList = reasonList;
        }
    }

    @Data
    @Builder
    public static class ReasonItemDTO {
        private Integer id;
        private String code;
        private String label;

        public static ReasonItemDTO from(ReportReasonCode reasonCode) {
            return ReasonItemDTO.builder()
                    .id(reasonCode.getId())
                    .code(reasonCode.name())
                    .label(reasonCode.getLabel())
                    .build();
        }
    }


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

    public class DetailDTO {

    }
}
