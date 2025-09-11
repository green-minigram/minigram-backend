package com.mtcoding.minigram.advertisements;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

public class AdvertisementResponse {

    @Data
    @AllArgsConstructor
    public static class SavedDTO {
        private final Integer adId;
        private final Integer postId;
        private final Integer userId;
        private final AdvertisementStatus status;
        private final LocalDateTime startAt;
        private final LocalDateTime endAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static SavedDTO from(Advertisement ad) {
            return new SavedDTO(
                    ad.getPostId(),
                    ad.getPost().getId(),
                    ad.getUser().getId(),
                    ad.getStatus(),
                    ad.getStartAt(),
                    ad.getEndAt(),
                    ad.getCreatedAt(),
                    ad.getUpdatedAt()
            );
        }
    }
}
