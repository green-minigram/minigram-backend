package com.mtcoding.minigram.stories;

import com.mtcoding.minigram.users.User;
import lombok.Data;

public class StoryRequest {

    @Data
    public static class CreateDTO {
        private String videoUrl;
        private String thumbnailUrl;

        public Story toEntity(User user) {
            return Story.builder()
                    .user(user)
                    .videoUrl(videoUrl)
                    .thumbnailUrl(thumbnailUrl)
                    .status(StoryStatus.ACTIVE)
                    .build();
        }
    }
}
