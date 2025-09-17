package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.constants.CommentConstants;
import com.mtcoding.minigram.stories.StoryStatus;
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

    public List<Object[]> findAllByPostId(Integer page, Integer postId, Integer currentUserId) {
        return em.createQuery("""
                        SELECT
                            c,
                            EXISTS (
                               SELECT 1 FROM CommentLike cl
                               WHERE cl.comment = c AND cl.user.id = :currentUserId
                            ) as isLiked,
                            (SELECT COUNT(cl2) FROM CommentLike cl2
                             WHERE cl2.comment = c) as likeCount,
                            EXISTS (
                               SELECT 1
                               FROM Story s
                               WHERE s.user = u
                                 AND s.status = :storyActive
                                 AND (
                                      SELECT COUNT(s2) FROM Story s2
                                      WHERE s2.user = u
                                        AND s2.status = :storyActive
                                        AND (
                                             s2.createdAt > s.createdAt
                                          OR (s2.createdAt = s.createdAt AND s2.id > s.id)
                                        )
                                 ) < 5
                                 AND NOT EXISTS (
                                      SELECT 1 FROM StoryView sv
                                      WHERE sv.story = s AND sv.user.id = :currentUserId
                                 )
                            ) as hasUnseen
                        FROM Comment c
                        JOIN FETCH c.user u
                        WHERE c.post.id = :postId
                          AND c.status = :commentActive
                        ORDER BY
                            COALESCE(c.root.id, c.id) ASC,
                            CASE WHEN c.parent.id IS NULL THEN 0 ELSE 1 END ASC,
                            c.id ASC
                        """, Object[].class)
                .setParameter("postId", postId)
                .setParameter("currentUserId", currentUserId)
                .setParameter("commentActive", CommentStatus.ACTIVE)
                .setParameter("storyActive", StoryStatus.ACTIVE)
                .setFirstResult(page * CommentConstants.ITEMS_PER_PAGE)
                .setMaxResults(CommentConstants.ITEMS_PER_PAGE)
                .getResultList();
    }

    public Long countAllByPostId(Integer postId) {
        return em.createQuery("""
                        SELECT COUNT(c)
                        FROM Comment c
                        JOIN c.user u
                        WHERE c.post.id = :postId
                          AND c.status = :commentActive
                        """, Long.class)
                .setParameter("postId", postId)
                .setParameter("commentActive", CommentStatus.ACTIVE)
                .getSingleResult();
    }


    public List<Object[]> findRepliesByRoot(Integer page, Integer rootId, Integer currentUserId) {
        return em.createQuery("""
                        SELECT
                            c,
                            CASE WHEN EXISTS (
                                SELECT 1
                                FROM CommentLike cl
                                WHERE cl.comment = c
                                  AND cl.user.id = :currentUserId
                            ) THEN true ELSE false END AS isLiked,
                            (SELECT COUNT(cl2)
                             FROM CommentLike cl2
                             WHERE cl2.comment = c) AS likeCount,
                            EXISTS (
                                SELECT 1
                                FROM Story s
                                WHERE s.user = u
                                  AND s.status = :storyActive
                                  AND (
                                      SELECT COUNT(s2)
                                      FROM Story s2
                                      WHERE s2.user = u
                                        AND s2.status = :storyActive
                                        AND (
                                             s2.createdAt > s.createdAt
                                          OR (s2.createdAt = s.createdAt AND s2.id > s.id)
                                        )
                                  ) < 5
                                  AND NOT EXISTS (
                                      SELECT 1
                                      FROM StoryView sv
                                      WHERE sv.story = s
                                        AND sv.user.id = :currentUserId
                                  )
                            ) AS hasUnseen
                        FROM Comment c
                        JOIN FETCH c.user u
                        WHERE c.root.id = :rootId
                          AND c.parent IS NOT NULL
                          AND c.status = :commentActive
                        ORDER BY c.createdAt ASC, c.id ASC
                        """, Object[].class)
                .setParameter("rootId", rootId)
                .setParameter("currentUserId", currentUserId)
                .setParameter("commentActive", CommentStatus.ACTIVE)
                .setParameter("storyActive", StoryStatus.ACTIVE)
                .setFirstResult(page * CommentConstants.ITEMS_PER_PAGE)
                .setMaxResults(CommentConstants.ITEMS_PER_PAGE)
                .getResultList();
    }

    public Long countRepliesByRoot(Integer rootId) {
        return em.createQuery("""
                        SELECT COUNT(c)
                        FROM Comment c
                        JOIN c.user u
                        WHERE c.root.id = :rootId
                          AND c.parent IS NOT NULL
                          AND c.status = :commentActive
                        """, Long.class)
                .setParameter("rootId", rootId)
                .setParameter("commentActive", CommentStatus.ACTIVE)
                .getSingleResult();
    }


    public Optional<Comment> findById(Integer commentId) {
        return Optional.ofNullable(em.find(Comment.class, commentId));
    }

    public Comment save(Comment comment) {
        em.persist(comment);
        return comment;
    }
}
