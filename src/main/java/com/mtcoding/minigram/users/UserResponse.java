package com.mtcoding.minigram.users;

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
        private List<PostItemDTO> postList;

        public PostListDTO(List<PostItemDTO> postList) {
            this.postList = postList;
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
}
