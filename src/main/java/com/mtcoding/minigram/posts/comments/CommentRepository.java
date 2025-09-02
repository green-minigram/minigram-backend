package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepository {
    private final EntityManager em;
}
