package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.users.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("s/api/posts/{postId}/comments")
public class CommentsController {
    private final CommentService commentService;
    private final HttpSession session;

    @GetMapping
    public List<CommentResponse.ItemDTO> getComments(@PathVariable Integer postId) {
        User sessionUser = (User) session.getAttribute("sessionUser");
        return commentService.findAllByPostId(postId, sessionUser);
    }
}
