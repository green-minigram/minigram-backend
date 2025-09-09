package com.mtcoding.minigram.stories.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StoryLikeRepository {
    private final EntityManager em;
}
