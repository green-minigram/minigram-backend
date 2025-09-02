package com.mtcoding.minigram.posts.comments;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentsController {
    private final CommentService commentService;
    private final HttpSession session;
}
