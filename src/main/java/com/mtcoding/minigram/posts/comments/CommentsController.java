package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
