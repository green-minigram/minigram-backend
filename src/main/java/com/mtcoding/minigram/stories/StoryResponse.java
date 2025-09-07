package com.mtcoding.minigram.stories;

import com.mtcoding.minigram.users.User;
import lombok.Data;

import java.time.LocalDateTime;

public class StoryResponse {

    @Data
    public static class DetailDTO {
        private UserDTO user;
        private StoryDTO story;
        private Boolean isFollowing;
        private Boolean isOwner;
        private Boolean isLiked;
        private Integer likeCount;

        @Data
        public static class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;

            public UserDTO(User user) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
            }
        }

        @Data
        public static class StoryDTO {
            private Integer storyId;
            private String videoUrl;
            private String thumbnailUrl;
            private LocalDateTime createdAt;

            public StoryDTO(Story story) {
                this.storyId = story.getId();
                this.videoUrl = story.getVideoUrl();
                this.thumbnailUrl = story.getThumbnailUrl();
                this.createdAt = story.getCreatedAt();
            }
        }

        public DetailDTO(Story story, Boolean isFollowing, Boolean isOwner, Boolean isLiked, Integer likeCount) {
            this.user = new UserDTO(story.getUser());
            this.story = new StoryDTO(story);
            this.isFollowing = isFollowing;
            this.isOwner = isOwner;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
        }
    }

    @Data
    public static class ListDTO {

    }
}
