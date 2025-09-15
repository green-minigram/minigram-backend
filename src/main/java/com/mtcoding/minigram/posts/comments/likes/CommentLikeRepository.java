package com.mtcoding.minigram.posts.comments.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class CommentLikeRepository {

    private final EntityManager em;

    //commentId -> likeCount 맵
    public Map<Integer, Integer> countByCommentIds(List<Integer> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) return Map.of();

        List<Object[]> rows = em.createQuery(
                        """
                                select cl.comment.id, count(cl)
                                from CommentLike cl
                                where cl.comment.id in :ids
                                group by cl.comment.id
                                """,
                        Object[].class
                )
                .setParameter("ids", commentIds)
                .getResultList();

        Map<Integer, Integer> map = new HashMap<>(rows.size());
        for (Object[] r : rows) {
            map.put((Integer) r[0], ((Long) r[1]).intValue());
        }
        return map;
    }

    // viewer가 누른 commentId 집합
    public Set<Integer> findLikedCommentIdsByUser(Integer userId, List<Integer> commentIds) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) return Set.of();

        List<Integer> rows = em.createQuery(
                        """
                                select distinct cl.comment.id
                                from CommentLike cl
                                where cl.user.id = :uid
                                  and cl.comment.id in :ids
                                """,
                        Integer.class
                )
                .setParameter("uid", userId)
                .setParameter("ids", commentIds)
                .getResultList();

        return new HashSet<>(rows);
    }

    public void save(CommentLike like) {
        em.persist(like);
    }

    public void delete(CommentLike like) {
        em.remove(like);
    }

    public Optional<CommentLike> findByCommentIdAndUserId(Integer commentId, Integer userId) {
        return em.createQuery("""
                        select cl from CommentLike cl
                        where cl.comment.id = :cid and cl.user.id = :uid
                        """, CommentLike.class)
                .setParameter("cid", commentId)
                .setParameter("uid", userId)
                .getResultStream()
                .findFirst();
    }

    public int deleteByCommentIdAndUserId(Integer commentId, Integer userId) {
        return em.createQuery("""
                        delete from CommentLike cl
                        where cl.comment.id = :cid and cl.user.id = :uid
                        """)
                .setParameter("cid", commentId)
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int countByCommentId(Integer commentId) {
        Long cnt = em.createQuery("""
                        select count(cl) from CommentLike cl
                        where cl.comment.id = :cid
                        """, Long.class)
                .setParameter("cid", commentId)
                .getSingleResult();
        return cnt.intValue();
    }
}
