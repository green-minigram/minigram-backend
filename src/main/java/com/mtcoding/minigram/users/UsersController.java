package com.mtcoding.minigram.users;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.posts.PostService;
import com.mtcoding.minigram.stories.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UsersController {
    private final UserService userService;
    private final PostService postService;
    private final StoryService storyService;

    // 유서 상세 페이지 다른 유저 프로필 (본인도 가능)
    @GetMapping("/s/api/users/{userId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable Integer userId, @AuthenticationPrincipal User user) {
        UserResponse.ProfileDTO respDTO = userService.getUserProfile(userId, user.getId());
        return Resp.ok(respDTO);
    }

    // 유저 상세페이지 본인 프로필
    @GetMapping("/s/api/users/me/profile")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal User user) {
        UserResponse.ProfileDTO respDTO = userService.getUserProfile(null, user.getId());
        return Resp.ok(respDTO);
    }

    // 유저 상세 페이지의 다른 유저 게시글 목록
    @GetMapping("/s/api/users/{userId}/posts")
    public ResponseEntity<?> getUserPosts(
            @PathVariable Integer userId,
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        UserResponse.PostListDTO respDTO = postService.getUserPost(userId, user.getId(), page);
        return Resp.ok(respDTO);
    }

    // 유저 상세 페이지의 본인 게시글 목록
    @GetMapping("/s/api/users/me/posts")
    public ResponseEntity<?> getMyPosts(
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        UserResponse.PostListDTO respDTO = postService.getUserPost(null, user.getId(), page);
        return Resp.ok(respDTO);
    }

    // 유저 상세 페이지의 다른 유저 스토리 목록
    @GetMapping("/s/api/users/{userId}/stories")
    public ResponseEntity<?> getUserStories(
            @PathVariable Integer userId,
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        UserResponse.StoryListDTO respDTO = storyService.getUserStories(userId, user.getId(), page);
        return Resp.ok(respDTO);
    }

    // 유저 상세 페이지의 본인 스토리 목록
    @GetMapping("/s/api/users/me/stories")
    public ResponseEntity<?> getMyStories(
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        UserResponse.StoryListDTO respDTO = storyService.getUserStories(null, user.getId(), page);
        return Resp.ok(respDTO);
    }
}
