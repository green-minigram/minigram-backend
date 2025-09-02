package com.mtcoding.minigram.posts.comments.likes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentLikesController {
    private final CommentLikeService commentLikeService;
    private final HttpSession session;
}
