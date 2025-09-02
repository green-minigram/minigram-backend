package com.mtcoding.minigram.posts.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikeRepository {
    private final EntityManager em;
}
