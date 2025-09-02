package com.mtcoding.minigram.posts;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;
}
