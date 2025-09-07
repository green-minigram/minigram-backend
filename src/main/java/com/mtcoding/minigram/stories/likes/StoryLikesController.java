package com.mtcoding.minigram.stories.likes;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StoryLikesController {
    private final StoryLikeService postLikeService;
}
