package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class CommentsController {
    private final CommentService commentService;

    // 게시글 기준 댓글 전체 목록 조회
    @GetMapping("/s/api/posts/{postId}/comments")
    public ResponseEntity<?> findAllByPostId(
            @PathVariable Integer postId,
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        CommentResponse.ListDTO respDTO = commentService.findAllByPostId(page, postId, user.getId());
        return Resp.ok(respDTO);
    }

    @DeleteMapping("/s/api/comments/{commentId}")
    public ResponseEntity<?> delete(@PathVariable Integer commentId, @AuthenticationPrincipal User user) {
        CommentResponse.DeleteDTO respDTO = commentService.delete(commentId, user.getId(), user.getRoles());
        return Resp.ok(respDTO);
    }

    // root 기준 대댓글 전체 목록 조회
    @GetMapping("/s/api/comments/{commentId}/replies")
    public ResponseEntity<?> findRepliesByRoot(
            @PathVariable Integer commentId,
            @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
            @AuthenticationPrincipal User user) {
        CommentResponse.ListDTO respDTO = commentService.findRepliesByRoot(page, commentId, user.getId());
        return Resp.ok(respDTO);
    }

    // 댓글 작성
    @PostMapping("/s/api/posts/{postId}/comments")
    public ResponseEntity<?> create(@PathVariable Integer postId, @Valid @RequestBody CommentRequest.CreateDTO reqDTO, @AuthenticationPrincipal User user) {
        CommentResponse.DTO respDTO = commentService.create(postId, reqDTO, user.getId());
        return Resp.ok(respDTO);
    }
}
