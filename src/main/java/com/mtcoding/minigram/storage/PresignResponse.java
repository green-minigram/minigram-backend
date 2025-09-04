package com.mtcoding.minigram.storage;

import lombok.Data;

public class PresignResponse {

    @Data
    public static class UploadDTO {
        public String key;         // S3 object key
        public String presignedUrl;   // 클라이언트가 PUT할 presigned URL
        public int expiresIn;   // TTL(sec)
        public String mimeType; // 클라이언트가 PUT할 때 동일 헤더 필수(서명 포함)

        public UploadDTO(String key, String presignedUrl, int expiresIn, String mimeType) {
            this.key = key;
            this.presignedUrl = presignedUrl;
            this.expiresIn = expiresIn;
            this.mimeType = mimeType;
        }
    }
}
