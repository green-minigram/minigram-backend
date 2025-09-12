package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.util.Resp;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UserService userService;

    @GetMapping("/s/api/users/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        UserResponse.ProfileDTO respDTO = userService.getUserProfile(userId, user.getId());
        return Resp.ok(respDTO);
    }

    @GetMapping("/s/api/users/me/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal User user) {
        UserResponse.ProfileDTO respDTO = userService.getUserProfile(null, user.getId());
        return Resp.ok(respDTO);
    }
}
