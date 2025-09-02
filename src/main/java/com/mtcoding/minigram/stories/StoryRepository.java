package com.mtcoding.minigram.stories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StoryRepository {
    private final EntityManager em;
}
