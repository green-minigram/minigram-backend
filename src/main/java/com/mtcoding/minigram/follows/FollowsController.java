package com.mtcoding.minigram.follows;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
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
    public ResponseEntity<?> create(@PathVariable Integer followeeId, @AuthenticationPrincipal User user) {
        FollowResponse.DTO respDTO = followService.create(user.getId(), followeeId);
        return Resp.ok(respDTO);
    }

    // 팔로우 취소
    @DeleteMapping("/s/api/users/{followeeId}/follow")
    public ResponseEntity<?> delete(@PathVariable Integer followeeId, @AuthenticationPrincipal User user) {
        FollowResponse.DeleteDTO respDTO = followService.delete(user.getId(), followeeId);
        return Resp.ok(respDTO);
    }

    // 팔로우 목록 (userId를 팔로우 하는 사람들, followeeId를 기준으로 follower들을 모음)
    @GetMapping("/s/api/users/{userId}/followers")
    public ResponseEntity<?> findFollowers(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        FollowResponse.ListDTO respDTO = followService.findFollowers(user.getId(), userId);
        return Resp.ok(respDTO);
    }

    // 팔로잉 목록 (userId가 팔로우 하는 사람들, followerId를 기준으로 followee들을 모음)
    @GetMapping("/s/api/users/{userId}/following")
    public ResponseEntity<?> findFollowing(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        FollowResponse.ListDTO respDTO = followService.findFollowing(user.getId(), userId);
        return Resp.ok(respDTO);
    }
}
