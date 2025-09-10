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
}
