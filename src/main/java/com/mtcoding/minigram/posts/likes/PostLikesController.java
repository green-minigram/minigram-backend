package com.mtcoding.minigram.posts.likes;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s/api/posts/{postId}/likes")
public class PostLikesController {
    private final PostLikeService postLikeService;
    private final HttpSession session;

    @PostMapping
    public ResponseEntity<?> like(@PathVariable Integer postId,
                                  @AuthenticationPrincipal User user) {
        var dto = postLikeService.like(postId, user.getId());
        return Resp.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@PathVariable Integer postId,
                                    @AuthenticationPrincipal User user) {
        var dto = postLikeService.unlike(postId, user.getId());
        return Resp.ok(dto);
    }
}
