package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.util.JwtUtil;
import com.mtcoding.minigram._core.util.Resp;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UserService userService;
    private final HttpSession session;

    // 임시 유저 토큰 발급 (1 : 관리자 / 2 : ssar (일반유저))
    @PostMapping("/users/{userId}/token")
    public ResponseEntity<?> tempLogin(@PathVariable Integer userId) {
        User tempUser = User.builder()
                .id(userId)
                .username(userId == 1 ? "minigram" : "ssar")
                .roles(userId == 1 ? "ADMIN" : "USER")
                .build();

        String accessToken = JwtUtil.create(tempUser);

        Map<String, Object> body = Map.of("accessToken", accessToken);

        return Resp.ok(body);
    }
}
