package com.mtcoding.minigram.posts;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    public Integer findAuthorIdByPostId(Integer postId) {
        try {
            return em.createQuery(
                            "select p.user.id from Post p where p.id = :id", Integer.class)
                    .setParameter("id", postId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // 못 찾으면 null (isPostAuthor 계산시 false 처리됨)
        }
    }
}
