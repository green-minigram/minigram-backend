package com.mtcoding.minigram.users;

import lombok.Data;

public class UserResponse {

    @Data
    public static class SummaryDTO {
        private Integer userId;
        private String username;
        private String profileImageUrl;

        public SummaryDTO(User user) {
            this.userId = user.getId();
            this.username = user.getUsername();
            this.profileImageUrl = user.getProfileImageUrl();
        }
    }
}
