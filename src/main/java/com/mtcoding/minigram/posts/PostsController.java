package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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

    @DeleteMapping("/s/api/posts/{postId}")
    public ResponseEntity<?> delete(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        PostResponse.DeleteDTO respDTO = postService.delete(postId, user.getId());
        return Resp.ok(respDTO);
    }
}
