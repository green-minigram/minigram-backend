package com.mtcoding.minigram.stories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StoryRepository {
    private final EntityManager em;

    public Optional<Object[]> findByStoryId(Integer storyId, Integer currentUserId) {
        try{
            Object[] objects = em.createQuery("""
                      SELECT\s
                        s,
                        (CASE WHEN COUNT(f) > 0 THEN true ELSE false END) AS isFollowing,
                        COUNT(DISTINCT slAll.id)                             AS likeCount,
                        (CASE WHEN COUNT(slMine) > 0 THEN true ELSE false END) AS isLiked
                      FROM Story s
                      JOIN FETCH s.user u
                      LEFT JOIN Follow f\s
                             ON f.followee = u AND f.follower.id = :currentUserId
                      LEFT JOIN StoryLike slAll\s
                             ON slAll.story = s
                      LEFT JOIN StoryLike slMine\s
                             ON slMine.story = s AND slMine.user.id = :currentUserId
                      WHERE s.id = :storyId
                        AND s.status = :status
                      GROUP BY s, u
                   \s""", Object[].class)
                    .setParameter("storyId", storyId)
                    .setParameter("currentUserId", currentUserId)
                    .setParameter("status", StoryStatus.ACTIVE)
                    .getSingleResult();
            return Optional.of(objects);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public List<Object[]> findMyStories(Integer currentUserId) {
        List<Object[]> obsList =  em.createQuery("""
                    SELECT
                      s,
                      false AS isFollowing,
                      COUNT(DISTINCT slAll.id) AS likeCount,
                      (CASE WHEN COUNT(slMine) > 0 THEN true ELSE false END) AS isLiked
                    FROM Story s
                    JOIN FETCH s.user u
                    LEFT JOIN StoryLike slAll ON slAll.story = s
                    LEFT JOIN StoryLike slMine ON slMine.story = s AND slMine.user.id = :currentUserId
                    WHERE u.id = :currentUserId
                      AND s.status = :status
                    GROUP BY s, u
                    ORDER BY s.createdAt DESC
                """, Object[].class)
                            .setParameter("currentUserId", currentUserId)
                            .setParameter("status", StoryStatus.ACTIVE)
                            .setMaxResults(5)
                            .getResultList();

        return obsList;
    }

//    public List<Object[]> findAllByUserId(Integer targetUserId, Integer currentUserId, int limit) {
//        return em.createQuery("""
//        SELECT
//          s,
//          (CASE WHEN COUNT(f) > 0 THEN true ELSE false END) AS isFollowing,
//          COUNT(DISTINCT slAll.id) AS likeCount,
//          (CASE WHEN COUNT(slMine) > 0 THEN true ELSE false END) AS isLiked
//        FROM Story s
//        JOIN FETCH s.user u
//        LEFT JOIN Follow f
//               ON f.followee = u AND f.follower.id = :currentUserId   -- 팔로우 여부
//        LEFT JOIN StoryLike slAll
//               ON slAll.story = s
//        LEFT JOIN StoryLike slMine
//               ON slMine.story = s AND slMine.user.id = :currentUserId
//        WHERE u.id = :targetUserId
//          AND s.status = :status
//        GROUP BY s, u
//        ORDER BY s.createdAt DESC
//    """, Object[].class)
//                .setParameter("targetUserId", targetUserId)
//                .setParameter("currentUserId", currentUserId)
//                .setParameter("status", StoryStatus.ACTIVE)
//                .setMaxResults(limit)
//                .getResultList();
//    }

}
