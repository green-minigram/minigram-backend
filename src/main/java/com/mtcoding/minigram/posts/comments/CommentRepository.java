package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepository {
    @PersistenceContext
    private final EntityManager em;

    public long countByPostId(Integer postId) {
        var jpql = "select count(c) from Comment c where c.post.id = :postId";
        return em.createQuery(jpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public List<Comment> findAllByPostId(Integer postId) {
        String jpql = "select c from Comment c join fetch c.user where c.post.id = :postId order by c.id desc";
        return em.createQuery(jpql, Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public List<Comment> findParentsByPostId(Integer postId) {
        String jpql = """
                    select c from Comment c
                    join fetch c.user
                    where c.post.id = :postId
                      and c.parent is null
                    order by c.createdAt asc, c.id asc
                """;
        return em.createQuery(jpql, Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    // [추가] 여러 부모의 자식들 한꺼번에
    public List<Comment> findChildrenByParentIds(List<Integer> parentIds) {
        if (parentIds.isEmpty()) return List.of();
        String jpql = """
                    select c from Comment c
                    join fetch c.user
                    where c.parent.id in :pids
                    order by c.createdAt asc, c.id asc
                """;
        return em.createQuery(jpql, Comment.class)
                .setParameter("pids", parentIds)
                .getResultList();
    }
}
