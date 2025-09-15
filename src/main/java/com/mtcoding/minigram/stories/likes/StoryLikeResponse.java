package com.mtcoding.minigram.stories.likes;

import lombok.Data;

import java.time.LocalDateTime;

public class StoryLikeResponse {

    @Data
    public static class CreateDTO {
        private Integer storyLikeId;
        private Integer storyId;
        private Integer userId;
        private Integer likeCount;
        private LocalDateTime createdAt;

        public CreateDTO(StoryLike storyLike, Integer likeCount) {
            this.storyLikeId = storyLike.getId();
            this.storyId = storyLike.getStory().getId();
            this.userId = storyLike.getUser().getId();
            this.likeCount = likeCount;
            this.createdAt = storyLike.getCreatedAt();
        }
    }
}
