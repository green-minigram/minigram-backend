package com.mtcoding.minigram.follows;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.stories.StoryRequest;
import com.mtcoding.minigram.stories.StoryResponse;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowsController {
    private final FollowService followService;

    // 팔로우
    @PostMapping("/s/api/follows")
    public ResponseEntity<?> create(@RequestBody FollowRequest.CreateDTO reqDTO, @AuthenticationPrincipal User user) {
        FollowResponse.DTO respDTO = followService.create(reqDTO, user);
        return Resp.ok(respDTO);
    }
}
