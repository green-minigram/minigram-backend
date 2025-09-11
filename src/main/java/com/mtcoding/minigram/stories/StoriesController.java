package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.posts.PostResponse;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class StoriesController {
    private final StoryService storyService;

    // 스토리 상세
    @GetMapping("/s/api/stories/{storyId}")
    public ResponseEntity<?> findByStoryId(@PathVariable Integer storyId, @AuthenticationPrincipal User user) {
        StoryResponse.DetailDTO respDTO = storyService.findByStoryId(storyId, user.getId());
        return Resp.ok(respDTO);
    }

    // 로그인 유저의 스토리 목록 (최근 5개)
    @GetMapping("/s/api/users/me/stories")
    public ResponseEntity<?> findAllMyStories(@AuthenticationPrincipal User user) {
        StoryResponse.ListDTO respDTO = storyService.findAllMyStories(user.getId());
        return Resp.ok(respDTO);
    }

    // 특정 유저의 스토리 목록 (최근 5개)
    @GetMapping("/s/api/users/{userId}/stories")
    public ResponseEntity<?> findAllByUserId(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        StoryResponse.ListDTO respDTO = storyService.findAllByUserId(userId, user.getId());
        return Resp.ok(respDTO);
    }

    // 스토리 등록
    @PostMapping("/s/api/stories")
    public ResponseEntity<?> create(@RequestBody StoryRequest.CreateDTO reqDTO, @AuthenticationPrincipal User user) {
        StoryResponse.DTO respDTO = storyService.create(reqDTO, user);
        return Resp.ok(respDTO);
    }

    // 스토리 삭제
    @DeleteMapping("/s/api/stories/{storyId}")
    public ResponseEntity<?> delete(@PathVariable Integer storyId, @AuthenticationPrincipal User user) {
        StoryResponse.DTO respDTO = storyService.delete(storyId, user.getId());
        return Resp.ok(respDTO);
    }

    // 피드 스토리 헤더 조회
    @GetMapping("/s/api/feed/stories")
    public ResponseEntity<?> getFeedStories(@RequestParam(required = false, value = "page", defaultValue = "0") Integer page, @AuthenticationPrincipal User user) {
        StoryResponse.FeedDTO respDTO = storyService.getFeedStories(page, user.getId());
        return Resp.ok(respDTO);
    }
}
