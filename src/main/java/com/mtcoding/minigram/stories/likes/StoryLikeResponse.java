package com.mtcoding.minigram.stories.likes;

import lombok.Data;

import java.time.LocalDateTime;

public class StoryLikeResponse {

    @Data
    public static class CreateDTO {
        private Integer storyLikeId;
        private Integer storyId;
        private Integer userId;
        private Boolean isLiked;
        private Integer likeCount;
        private LocalDateTime createdAt;

        public CreateDTO(StoryLike storyLike, Integer likeCount) {
            this.storyLikeId = storyLike.getId();
            this.storyId = storyLike.getStory().getId();
            this.userId = storyLike.getUser().getId();
            this.isLiked = true;
            this.likeCount = likeCount;
            this.createdAt = storyLike.getCreatedAt();
        }
    }

    @Data
    public static class DeleteDTO {
        private Integer storyId;
        private Boolean isLiked;
        private Integer likeCount;
        private String message;

        public DeleteDTO(Integer storyId, Integer likeCount) {
            this.storyId = storyId;
            this.isLiked = false;
            this.likeCount = likeCount;
            this.message = "스토리에 대한 좋아요를 취소했습니다";
        }
    }
}
