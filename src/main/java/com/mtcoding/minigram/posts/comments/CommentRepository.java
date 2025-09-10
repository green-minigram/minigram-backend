package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommentRepository {

    private final EntityManager em;

    // 게시글별 댓글 수
    public long countByPostId(Integer postId) {
        return em.createQuery(
                        "select count(c) from Comment c " +
                                "where c.post.id = :postId and c.status = :active",
                        Long.class
                )
                .setParameter("postId", postId)
                .setParameter("active", CommentStatus.ACTIVE)
                .getSingleResult();
    }

    // 부모 댓글만 (최상위) - 오래된 순
    public List<Comment> findParentsByPostId(Integer postId) {
        return em.createQuery(
                        """
                                select c from Comment c
                                join fetch c.user
                                where c.post.id = :postId
                                  and c.parent is null
                                  and c.status = :active
                                order by c.createdAt asc, c.id asc
                                """,
                        Comment.class
                )
                .setParameter("postId", postId)
                .setParameter("active", CommentStatus.ACTIVE)
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
                                  and c.status = :active
                                order by c.createdAt asc, c.id asc
                                """,
                        Comment.class
                )
                .setParameter("pids", parentIds)
                .setParameter("active", CommentStatus.ACTIVE)
                .getResultList();
    }

    public Optional<Comment> findCommentById(Integer id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    // 삭제 권한 판단용 단건 조회 (댓글 + 작성자 + 게시글 + 게시글 작성자)
    public Optional<Comment> findWithPostAndUsersById(Integer commentId) {
        return em.createQuery(
                        """
                                select c
                                from Comment c
                                join fetch c.user         
                                join fetch c.post p
                                join fetch p.user         
                                where c.id = :id
                                """,
                        Comment.class
                )
                .setParameter("id", commentId)
                .getResultStream()
                .findFirst();
    }
}
