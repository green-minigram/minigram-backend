package com.mtcoding.minigram.stories;

import lombok.Data;

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
            private Integer id;
            private String username;
            private String profileImageUrl;
        }

        @Data
        public static class StoryDTO {
            private Integer storyId;
            private String videoUrl;
            private String thumbnailUrl;
            private String createdAt;
        }
        
    }

    @Data
    public static class ListDTO {

    }
}
