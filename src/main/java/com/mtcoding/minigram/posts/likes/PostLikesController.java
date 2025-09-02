package com.mtcoding.minigram.posts.likes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostLikesController {
    private final PostLikeService postLikeService;
    private final HttpSession session;
}
