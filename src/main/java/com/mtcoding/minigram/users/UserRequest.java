package com.mtcoding.minigram.users;

import lombok.Data;

public class UserRequest {

    @Data
    public static class JoinDTO {
        private String email;
        private String username;
        private String password;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(email)
                    .username(username)
                    .roles("USER")
                    .password(encodedPassword)
                    .build();
        }
    }


    @Data
    public static class LoginDTO {
        private String email;
        private String password;
    }
}
