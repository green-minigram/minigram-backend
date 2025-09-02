package com.mtcoding.minigram.follows;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FollowsController {
    private final FollowService followService;
    private final HttpSession session;
}
