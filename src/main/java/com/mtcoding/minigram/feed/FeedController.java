package com.mtcoding.minigram.feed;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FeedController {
    private final FeedService feedService;

    // 피드 게시글 조회
    @GetMapping("/s/api/feed/posts")
    public ResponseEntity<?> findPosts(@RequestParam(required = false, value = "page", defaultValue = "0") Integer page, @AuthenticationPrincipal User user) {
        FeedResponse.PostListDTO respDTO = feedService.findPosts(page, user.getId());
        return Resp.ok(respDTO);
    }

    // 피드 스토리 프리뷰 조회
    @GetMapping("/s/api/feed/story-previews")
    public ResponseEntity<?> findStoryPreviews(@RequestParam(required = false, value = "page", defaultValue = "0") Integer page, @AuthenticationPrincipal User user) {
        FeedResponse.PreviewListDTO respDTO = feedService.findStoryPreviews(page, user.getId());
        return Resp.ok(respDTO);
    }

    // 로그인 유저의 스토리 목록 (최근 5개)
    @GetMapping("/s/api/feed/users/me/stories")
    public ResponseEntity<?> findMyStories(@AuthenticationPrincipal User user) {
        FeedResponse.StoryListDTO respDTO = feedService.findMyStories(user.getId());
        return Resp.ok(respDTO);
    }

    // 특정 유저의 스토리 목록 (최근 5개)
    @GetMapping("/s/api/feed/users/{userId}/stories")
    public ResponseEntity<?> findStoriesByUserId(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        FeedResponse.StoryListDTO respDTO = feedService.findStoriesByUserId(userId, user.getId());
        return Resp.ok(respDTO);
    }
}
