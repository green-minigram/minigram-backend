package com.mtcoding.minigram.storage;

import lombok.Data;

public class PresignResponse {

    @Data
    public static class UploadDTO {
        public String key;         // S3 object key
        public String presignedUrl;
        public int expiresIn;   // TTL(sec)
        public String mimeType;

        public UploadDTO(String key, String presignedUrl, int expiresIn, String mimeType) {
            this.key = key;
            this.presignedUrl = presignedUrl;
            this.expiresIn = expiresIn;
            this.mimeType = mimeType;
        }
    }
}
