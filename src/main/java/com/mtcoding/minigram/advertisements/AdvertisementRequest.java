package com.mtcoding.minigram.advertisements;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AdvertisementRequest {

    @Data
    @NoArgsConstructor
    public static class CreateDTO {   // JSON 전용 (파일 업로드 없음)
        private String content;       // 캡션
        private List<String> imageUrls; // Presign 업로드 후 최종 URL들
        private LocalDateTime startAt; // 광고 시작일
        private LocalDateTime endAt; // 광고 종료일
    }

}
