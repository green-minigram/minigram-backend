package com.mtcoding.minigram.posts.comments.likes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
@Repository
public class CommentLikeRepository {
    @PersistenceContext
    private final EntityManager em;


    /**
     * commentId -> likeCount
     */
    public Map<Integer, Integer> countByCommentIds(List<Integer> commentIds) {
        if (commentIds.isEmpty()) return Map.of();
        List<Object[]> rows = em.createQuery("""
                            select cl.comment.id, count(cl)
                            from CommentLike cl
                            where cl.comment.id in :ids
                            group by cl.comment.id
                        """, Object[].class)
                .setParameter("ids", commentIds)
                .getResultList();

        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put((Integer) r[0], ((Long) r[1]).intValue());
        }
        return map;
    }

    /**
     * viewer가 누른 commentId 집합
     */
    public Set<Integer> findLikedCommentIdsByUser(Integer userId, List<Integer> commentIds) {
        if (userId == null || commentIds.isEmpty()) return Set.of();
        List<Integer> rows = em.createQuery("""
                            select distinct cl.comment.id
                            from CommentLike cl
                            where cl.user.id = :uid
                              and cl.comment.id in :ids
                        """, Integer.class)
                .setParameter("uid", userId)
                .setParameter("ids", commentIds)
                .getResultList();
        return new HashSet<>(rows);
    }
}
