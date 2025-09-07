package com.mtcoding.minigram.follows;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.stories.StoryRequest;
import com.mtcoding.minigram.stories.StoryResponse;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class FollowsController {
    private final FollowService followService;

    // 팔로우
    @PostMapping("/s/api/users/{followeeId}/follow")
    public ResponseEntity<?> create(@PathVariable Integer followeeId,  @AuthenticationPrincipal User user) {
        FollowResponse.DTO respDTO = followService.create(user.getId(), followeeId);
        return Resp.ok(respDTO);
    }

    // 팔로우 취소
    @DeleteMapping("/s/api/users/{followeeId}/follow")
    public ResponseEntity<?> delete(@PathVariable Integer followeeId, @AuthenticationPrincipal User user) {
        FollowResponse.DeleteDTO respDTO = followService.delete(user.getId(), followeeId);
        return Resp.ok(respDTO);
    }
}
