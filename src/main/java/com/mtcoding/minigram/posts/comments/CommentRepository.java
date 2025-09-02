package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentRepository {
    private final EntityManager em;

    public long countByPostId(Integer postId) {
        var jpql = "select count(c) from Comment c where c.post.id = :postId";
        return em.createQuery(jpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }
}
