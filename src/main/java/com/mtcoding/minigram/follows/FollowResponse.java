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
}
