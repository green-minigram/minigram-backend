package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostsController {
    private final PostService postService;
    private final HttpSession session;

    @GetMapping("/s/api/posts/{postId}")
    public ResponseEntity<?> find(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        PostResponse.DetailDTO respDTO = postService.find(postId, user.getId());
        return Resp.ok(respDTO);
    }

    @PostMapping("/s/api/posts")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @RequestBody PostRequest.CreateDTO reqDTO) {
        var respDTO = postService.create(reqDTO, user.getId());
        return Resp.ok(respDTO); // DetailDTO 반환
    }

    @DeleteMapping("/s/api/posts/{postId}")
    public ResponseEntity<?> delete(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        PostResponse.DeleteDTO respDTO = postService.delete(postId, user.getId());
        return Resp.ok(respDTO);
    }

    // 피드 게시글 조회
    @GetMapping("/s/api/feed/posts")
    public ResponseEntity<?> getFeedPosts(@RequestParam(required = false, value = "page", defaultValue = "0") Integer page, @AuthenticationPrincipal User user) {
        PostResponse.FeedDTO respDTO = postService.getFeedPosts(page, user.getId());
        return Resp.ok(respDTO);
    }
}
