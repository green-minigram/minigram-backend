package com.mtcoding.minigram.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class PresignRequest {

    @Data
    public static class UploadDTO {
        @NotNull
        private UploadType uploadType;            // IMAGE/VIDEO
        @NotBlank
        private String mimeType;            // image/jpeg, video/mp4 ...
    }
}
