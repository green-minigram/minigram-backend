package com.mtcoding.minigram.posts.comments;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentsController {
    private final CommentService commentService;
    private final HttpSession session;

    @GetMapping
    public List<CommentResponse.ItemDTO> getComments(@PathVariable Integer postId, @RequestParam(required = false) Integer viewerId) {
        return commentService.getCommentsByPostId(postId, viewerId);
    }
}
