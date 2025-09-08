package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.users.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class ReportRequest {

    @Data
    public static class SaveDTO {
        @NotNull(message = "신고 대상 유형은 필수이며, POST 또는 STORY만 가능합니다.")
        private ReportType reportType;   // POST / STORY

        @NotNull(message = "대상 ID는 필수입니다.")
        private Integer targetId;  // 신고 대상 PK

        @NotNull(message = "신고 사유는 필수입니다.")
        private Integer reportReasonId;  // 신고 사유 PK

        public Report toEntity(User reporter, ReportReason reportReason) {
            return Report.builder()
                    .type(reportType)
                    .targetId(targetId)
                    .reporter(reporter)  // 로그인 사용자(@AuthenticationPrincipal)
                    .reason(reportReason)
                    .status(ReportStatus.PENDING)
                    .build();
        }
    }
}