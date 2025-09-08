package com.mtcoding.minigram.users;

import lombok.Data;

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

}
