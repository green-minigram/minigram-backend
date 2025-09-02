package com.mtcoding.minigram.posts;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostsController {
    private final PostService postService;
    private final HttpSession session;
}
