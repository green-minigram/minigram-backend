package com.mtcoding.minigram.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class UserRequest {

    @Data
    public static class JoinDTO {
        @Email( message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일은 필수 입력 값입니다")
        private String email;

        @Pattern(
                regexp = "^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)[a-z0-9._]{2,20}$",
                message = "아이디는 영문, 숫자, 밑줄(_), 마침표(.)만 사용 가능합니다. (2~20자)"
        )
        @NotBlank(message = "아이디는 필수 입력 값입니다")
        private String username;

        @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다")
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
        @Email( message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일은 필수 입력 값입니다")
        private String email;

        @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다")
        private String password;
    }

    @Data
    public static class UpdateDTO {
        @Email( message = "이메일 형식이 올바르지 않습니다")
        private String email;

        @Pattern(
                regexp = "^(?!.*\\.\\.)(?!\\.)(?!.*\\.$)[a-z0-9._]{2,20}$",
                message = "아이디는 영문, 숫자, 밑줄(_), 마침표(.)만 사용 가능합니다. (2~20자)"
        )
        private String username;

        @Size(min = 4, max = 20, message = "비밀번호는 4~20자여야 합니다")
        private String password;

        @Size(min = 2, max = 20, message = "닉네임은 2~20자여야 합니다")
        @Pattern(
                regexp = "^(?!\\s)(?!.*\\s$)(?!.*\\s{2,})[a-zA-Z가-힣0-9._\\-\\s]+$",
                message = "닉네임은 한글/영문/숫자/공백/._-만 사용 가능합니다."
        )
        private String name;

        @Pattern(regexp = "MALE|FEMALE|OTHER",
                message = "gender는 MALE, FEMALE, OTHER 중 하나여야 합니다")
        private String gender;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthdate; // "yyyy-MM-dd"

        @URL(message = "프로필 이미지 URL 형식이 올바르지 않습니다")
        private String profileImageUrl;

        @Size(max = 150, message = "소개 글은 최대 150자까지 가능합니다")
        private String bio;
    }
}
