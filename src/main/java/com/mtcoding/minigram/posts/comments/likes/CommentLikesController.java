package com.mtcoding.minigram.posts.comments.likes;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s/api/comments/{commentId}/likes")
public class CommentLikesController {
    private final CommentLikeService commentLikeService;
    private final HttpSession session;

    @PostMapping
    public ResponseEntity<?> like(@PathVariable Integer commentId,
                                  @AuthenticationPrincipal User user) {
        var dto = commentLikeService.like(commentId, user.getId());
        return Resp.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<?> unlike(@PathVariable Integer commentId,
                                    @AuthenticationPrincipal User user) {
        var dto = commentLikeService.unlike(commentId, user.getId());
        return Resp.ok(dto);
    }
}
