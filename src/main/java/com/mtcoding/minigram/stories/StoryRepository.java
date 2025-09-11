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
        try {
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

    public List<Object[]> findAllMyStories(Integer currentUserId) {
        List<Object[]> obsList = em.createQuery("""
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

    public List<Object[]> findAllByUserId(Integer userId, Integer currentUserId) {
        List<Object[]> obsList = em.createQuery("""
                           SELECT
                             s,
                             (CASE WHEN COUNT(f) > 0 THEN true ELSE false END) AS isFollowing,
                             COUNT(DISTINCT slAll.id)                          AS likeCount,
                             (CASE WHEN COUNT(slMine) > 0 THEN true ELSE false END) AS isLiked
                           FROM Story s
                           JOIN FETCH s.user u
                           LEFT JOIN Follow f
                                  ON f.followee = u AND f.follower.id = :currentUserId
                           LEFT JOIN StoryLike slAll
                                  ON slAll.story = s 
                           LEFT JOIN StoryLike slMine
                                  ON slMine.story = s AND slMine.user.id = :currentUserId 
                           WHERE u.id = :userId
                             AND s.status = :status
                           GROUP BY s, u
                           ORDER BY s.createdAt DESC
                        """, Object[].class)
                .setParameter("userId", userId)
                .setParameter("currentUserId", currentUserId)
                .setParameter("status", StoryStatus.ACTIVE)
                .setMaxResults(5)
                .getResultList();

        return obsList;
    }


    public Story save(Story story) {
        em.persist(story);
        return story;
    }

    public Optional<Story> findById(Integer storyId) {
        return Optional.ofNullable(em.find(Story.class, storyId));
    }

    public boolean existsByUD(Integer targerId) {
        List<Integer> result = em.createQuery("""
                        select 1 from Story s where s.id = :stroyId
                        """, Integer.class)
                .setParameter("stroyId", targerId)
                .setMaxResults(1)
                .getResultList();
        return !result.isEmpty();
    }
}
