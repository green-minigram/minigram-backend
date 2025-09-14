package com.mtcoding.minigram.posts;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class PostRequest {

    @Data
    @NoArgsConstructor
    public static class CreateDTO {   // JSON 전용 (파일 업로드 없음)
        private String content;       // 캡션
        private List<String> imageUrls; // Presign 업로드 후 최종 URL들
    }

    @Data
    public static class UpdateDTO {
        // 둘 다 "옵션": null이면 해당 필드는 수정 안 함
        private String content;          // null 아니면 업데이트
        private List<String> imageUrls;  // null 아니면 전체 교체(1~10장 검증)
    }
}
