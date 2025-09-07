package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentsController {
    private final CommentService commentService;
    private final HttpSession session;

    @GetMapping("s/api/posts/{postId}/comments")
    public List<CommentResponse.ItemDTO> getComments(@PathVariable Integer postId, @AuthenticationPrincipal User user) {
        return commentService.findAllByPostId(postId, user.getId());
    }

    // ✅ 작성 (JSON 전용)
    @PostMapping(value = "s/api/posts/{postId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@PathVariable Integer postId,
                                    @AuthenticationPrincipal User user,
                                    @RequestBody CommentRequest.CreateDTO req) {
        var dto = commentService.create(postId, user.getId(), req);
        return Resp.ok(dto); // ItemDTO 반환
    }


}
