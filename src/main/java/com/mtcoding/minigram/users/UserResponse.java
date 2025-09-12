package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.constants.UserDetailConstants;
import lombok.Data;

import java.util.List;

public class UserResponse {
    @Data
    public static class JoinDTO {
        private Integer userId;
        private String username;
        private String roles;

        public JoinDTO(User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.roles = user.getRoles();
        }
    }

    @Data
    public static class LoginDTO {
        private String accessToken;
        private Integer userId;
        private String username;
        private String roles;

        public LoginDTO(String accessToken, User user) {
            this.accessToken = accessToken;
            this.userId = user.getId();
            this.username = user.getUsername();
            this.roles = user.getRoles();
        }
    }

    @Data
    public static class ProfileDTO {
        private Integer userId;
        private String username;
        private String bio;
        private String profileImageUrl;
        private Boolean hasUnseen;
        private Boolean isOwner;
        private Boolean isFollowing;
        private Integer postCount;
        private Integer followerCount;
        private Integer followingCount;

        public ProfileDTO(User user, Boolean hasUnseen, Boolean isOwner, Boolean isFollowing, Integer postCount, Integer followerCount, Integer followingCount) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.bio = user.getBio();
            this.profileImageUrl = user.getProfileImageUrl();
            this.hasUnseen = hasUnseen;
            this.isOwner = isOwner;
            this.isFollowing = isFollowing;
            this.postCount = postCount;
            this.followerCount = followerCount;
            this.followingCount = followingCount;
        }
    }

    @Data
    public static class PostListDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<PostItemDTO> postList;

        public PostListDTO(List<PostItemDTO> postList, Integer current, Integer totalCount) {
            this.postList = postList;
            this.current = current;
            this.size = UserDetailConstants.ITEMS_PER_PAGE;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size); // 2
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = totalPage == 0 || current.equals(totalPage - 1);
        }

        private Integer makeTotalPage(int totalCount, int size) {
            int rest = totalCount % size > 0 ? 1 : 0;
            return totalCount / size + rest;
        }
    }

    @Data
    public static class PostItemDTO {
        private Integer postId;
        private String postImageUrl;

        public PostItemDTO(Integer postId, String postImageUrl) {
            this.postId = postId;
            this.postImageUrl = postImageUrl;
        }
    }

    @Data
    public static class StoryListDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<StoryItemDTO> storyList;

        public StoryListDTO(List<StoryItemDTO> storyList, Integer current, Integer totalCount) {
            this.storyList = storyList;
            this.current = current;
            this.size = UserDetailConstants.ITEMS_PER_PAGE;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size); // 2
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = totalPage == 0 || current.equals(totalPage - 1);
        }

        private Integer makeTotalPage(int totalCount, int size) {
            int rest = totalCount % size > 0 ? 1 : 0;
            return totalCount / size + rest;
        }
    }

    @Data
    public static class StoryItemDTO {
        private Integer storyId;
        private String thumbnailUrl;

        public StoryItemDTO(Integer storyId, String thumbnailUrl) {
            this.storyId = storyId;
            this.thumbnailUrl = thumbnailUrl;
        }
    }
}
