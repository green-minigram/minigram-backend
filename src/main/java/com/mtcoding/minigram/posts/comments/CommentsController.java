package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("s/api/posts/{postId}/comments")
    public ResponseEntity<?> create(@PathVariable Integer postId, @AuthenticationPrincipal User user, @RequestBody CommentRequest.CreateDTO reqDTO) {
        CommentResponse.SavedDTO respDTO = commentService.create(postId, user.getId(), reqDTO);
        return Resp.ok(respDTO); // ItemDTO 반환
    }
}
