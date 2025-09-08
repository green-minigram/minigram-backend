package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.reports.reasons.ReportReason;
import com.mtcoding.minigram.users.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


public class ReportRequest {

    @Data
    public static class SaveDTO {
        @NotNull(message = "신고 대상 유형은 필수입니다.")
        private ReportType type;   // POST / STORY

        @NotNull(message = "대상 ID는 필수입니다.")
        private Integer targetId;  // 신고 대상 PK

        @NotNull(message = "신고 사유는 필수입니다.")
        private Integer reasonId;  // 신고 사유 PK

        public Report toEntity(User reporter, ReportReason reason) {
            return Report.builder()
                    .type(this.getType())
                    .targetId(this.getTargetId())
                    .reporter(reporter)  // 세션 사용자 기반
                    .reason(reason)
                    .status(ReportStatus.PENDING)
                    .build();
        }


    }
}