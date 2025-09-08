package com.mtcoding.minigram.follows;

import lombok.Data;

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
}
