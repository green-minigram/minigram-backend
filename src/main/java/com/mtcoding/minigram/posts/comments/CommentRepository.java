package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final EntityManager em;

    // 게시글별 댓글 수
    public long countByPostId(Integer postId) {
        return em.createQuery(
                        "select count(c) from Comment c where c.post.id = :postId",
                        Long.class
                )
                .setParameter("postId", postId)
                .getSingleResult();
    }

    // 게시글의 전체 댓글 (유저 join fetch)
    public List<Comment> findAllByPostId(Integer postId) {
        return em.createQuery(
                        "select c from Comment c " +
                                "join fetch c.user " +
                                "where c.post.id = :postId " +
                                "order by c.id desc",
                        Comment.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }

    // 부모 댓글만 (최상위) - 오래된 순
    public List<Comment> findParentsByPostId(Integer postId) {
        return em.createQuery(
                        """
                                select c from Comment c
                                join fetch c.user
                                where c.post.id = :postId
                                  and c.parent is null
                                order by c.createdAt asc, c.id asc
                                """,
                        Comment.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }

    // 여러 부모의 자식 댓글 한 번에 - 오래된 순
    public List<Comment> findChildrenByParentIds(List<Integer> parentIds) {
        if (parentIds == null || parentIds.isEmpty()) return List.of();
        return em.createQuery(
                        """
                                select c from Comment c
                                join fetch c.user
                                where c.parent.id in :pids
                                order by c.createdAt asc, c.id asc
                                """,
                        Comment.class
                )
                .setParameter("pids", parentIds)
                .getResultList();
    }

    public java.util.Optional<Comment> findCommentById(Integer id) {
        return java.util.Optional.ofNullable(em.find(Comment.class, id));
    }

    public void save(Comment comment) {
        em.persist(comment);
    }
}
