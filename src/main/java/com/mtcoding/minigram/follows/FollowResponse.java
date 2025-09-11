package com.mtcoding.minigram.follows;

import com.mtcoding.minigram.users.User;
import lombok.Data;

import java.util.List;

public class FollowResponse {

    @Data
    public static class DTO {
        private Integer followId;
        private Integer followerId;
        private Integer followeeId;

        public DTO(Follow follow) {
            this.followId = follow.getId();
            this.followerId = follow.getFollower().getId();
            this.followeeId = follow.getFollowee().getId();
        }
    }

    @Data
    public static class DeleteDTO {
        private Integer followeeId;
        private String message;

        public DeleteDTO(Integer followeeId) {
            this.followeeId = followeeId;
            this.message = "해당 유저에 대한 팔로우를 취소했습니다";
        }
    }

    @Data
    public static class ListDTO {
        private List<ItemDTO> userList;

        public ListDTO(List<ItemDTO> userList) {
            this.userList = userList;
        }
    }

    @Data
    public static class ItemDTO {
        private Integer userId;
        private String username;
        private String name;
        private String profileImageUrl;
        private Boolean isFollowing;
        private Boolean isMe;

        public ItemDTO(User user, Boolean isFollowing, Boolean isMe) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.name = user.getName();
            this.profileImageUrl = user.getProfileImageUrl();
            this.isFollowing = isFollowing;
            this.isMe = isMe;
        }
    }
}
