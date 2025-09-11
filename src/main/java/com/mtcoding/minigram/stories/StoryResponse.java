package com.mtcoding.minigram.stories;

import com.mtcoding.minigram.users.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
        public class UserDTO {
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
        private List<DetailDTO> storyList;

        public ListDTO(List<DetailDTO> storyList) {
            this.storyList = storyList;
        }
    }

    @Data
    public static class DTO {
        private Integer storyId;
        private Integer userId;
        private String videoUrl;
        private String thumbnailUrl;
        private StoryStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DTO(Story story) {
            this.storyId = story.getId();
            this.userId = story.getUser().getId();
            this.videoUrl = story.getVideoUrl();
            this.thumbnailUrl = story.getThumbnailUrl();
            this.status = story.getStatus();
            this.createdAt = story.getCreatedAt();
            this.updatedAt = story.getUpdatedAt();
        }
    }

    @Data
    public static class FeedDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<ItemDTO> storyHeadList;

        public FeedDTO(List<ItemDTO> storyHeadList, Integer current, Integer totalCount) {
            this.storyHeadList = storyHeadList;
            this.current = current;
            this.size = 10;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size);
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = (totalPage - 1) == current;
        }

        private int makeTotalPage(int totalCount, int size) {
            if (size <= 0) return 0;
            int rest = (totalCount % size) > 0 ? 1 : 0;
            return (totalCount / size) + rest;
        }
    }

    @Data
    public static class ItemDTO {
        private Integer userId;
        private String username;
        private String profileImageUrl;
        private Boolean hasUnseen; // 최근 5개의 스토리 중 한 개라도 안읽은 스토리가 있는지 여부

        public ItemDTO(Integer userId, String username, String profileImageUrl, Boolean hasUnseen) {
            this.userId = userId;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
            this.hasUnseen = hasUnseen;
        }
    }
}
