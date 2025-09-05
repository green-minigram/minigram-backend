package com.mtcoding.minigram.reports;

import com.mtcoding.minigram.reports.reasons.ReportReason;
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
        private ReportReason reasonId;  // 신고 사유 PK


    }
}