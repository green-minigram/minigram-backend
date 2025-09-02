package com.mtcoding.minigram.stories;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StoriesController {
    private final StoryService storyService;
    private final HttpSession session;
}
