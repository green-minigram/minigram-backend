package com.mtcoding.minigram.stories.likes;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StoryLikesController {
    private final StoryLikeService storyLikeService;

    // 스토리 좋아요
    @PostMapping("/s/api/stories/{storyId}/likes")
    public ResponseEntity<?> create(@PathVariable Integer storyId, @AuthenticationPrincipal User user) {
        StoryLikeResponse.CreateDTO respDTO = storyLikeService.create(storyId, user.getId());
        return Resp.ok(respDTO);
    }

    // 스토리 좋아요 취소
    @DeleteMapping("/s/api/stories/{storyId}/likes")
    public ResponseEntity<?> delete(@PathVariable Integer storyId, @AuthenticationPrincipal User user) {
        StoryLikeResponse.DeleteDTO respDTO = storyLikeService.delete(storyId, user.getId());
        return Resp.ok(respDTO);
    }
}
