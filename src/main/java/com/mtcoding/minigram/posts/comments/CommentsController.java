package com.mtcoding.minigram.posts.comments;

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
public class CommentsController {
    private final CommentService commentService;
    private final HttpSession session;

    @GetMapping("s/api/posts/{postId}/comments")
    public ResponseEntity<?> findAllByPostId(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        CommentResponse.ListDTO respDTO = commentService.findAllByPostId(postId, user.getId());
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/s/api/comments/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Integer commentId, @AuthenticationPrincipal User user) {
        CommentResponse.DeleteDTO respDTO = commentService.delete(commentId, user.getId(), user.getRoles());
        return Resp.ok(respDTO);
    }
}
