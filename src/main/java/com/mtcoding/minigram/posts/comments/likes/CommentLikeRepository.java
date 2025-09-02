package com.mtcoding.minigram.posts.comments.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentLikeRepository {
    private final EntityManager em;
}
