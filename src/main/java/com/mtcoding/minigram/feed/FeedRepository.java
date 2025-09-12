package com.mtcoding.minigram.feed;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.mtcoding.minigram.posts.PostStatus;
import com.mtcoding.minigram.stories.StoryStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FeedRepository {
    private final EntityManager em;

    public List<Object[]> findPostsFromFolloweesIncludingMine(Integer page, Integer currentUserId) {
        return em.createQuery("""
                        SELECT
                           p,
                           (SELECT COUNT(pl1.id) FROM PostLike pl1 WHERE pl1.post = p),
                           CASE WHEN EXISTS (
                                  SELECT pl2.id FROM PostLike pl2
                                   WHERE pl2.post = p AND pl2.user.id = :currentUserId
                                ) THEN true ELSE false END,
                           (SELECT COUNT(c1.id) FROM Comment c1 WHERE c1.post = p),
                           CASE WHEN u.id = :currentUserId THEN false
                                WHEN f.id IS NOT NULL THEN true
                                ELSE false END
                        FROM Post p
                        JOIN FETCH p.user u
                        LEFT JOIN Follow f
                               ON f.followee = u
                              AND f.follower.id = :currentUserId
                        WHERE p.status = :status
                          AND (u.id = :currentUserId OR f.id IS NOT NULL)
                        ORDER BY p.createdAt DESC, p.id DESC
                        """, Object[].class)
                .setParameter("currentUserId", currentUserId)
                .setParameter("status", PostStatus.ACTIVE)
                .setFirstResult(page * FeedConstants.POSTS_PER_PAGE)
                .setMaxResults(FeedConstants.POSTS_PER_PAGE)
                .getResultList();
    }

    public Long getPostCountFromFolloweesIncludingMine(Integer currentUserId) {
        return em.createQuery("""
                        SELECT COUNT(DISTINCT p)
                        FROM Post p
                        JOIN p.user u
                        LEFT JOIN Follow f
                               ON f.followee = u
                              AND f.follower.id = :currentUserId
                        WHERE p.status = :status
                          AND (u.id = :currentUserId OR f.id IS NOT NULL)
                        """, Long.class)
                .setParameter("currentUserId", currentUserId)
                .setParameter("status", PostStatus.ACTIVE)
                .getSingleResult();
    }

    public List<Object[]> findStoriesFromFollowees(Integer page, Integer currentUserId) {
        return em.createQuery("""
                        WITH latest AS (
                          SELECT
                            s.id         as story_id,
                            s.user.id    as user_id,
                            s.createdAt  as created_at,
                            ROW_NUMBER() OVER (
                              PARTITION BY s.user.id
                              ORDER BY s.createdAt DESC, s.id DESC
                            ) AS rn
                          FROM Story s
                          WHERE s.status = 'ACTIVE'
                        )
                        SELECT
                          u.id,
                          u.username,
                          u.profileImageUrl,
                          CASE
                            WHEN SUM(CASE
                                       WHEN l.story_id IS NOT NULL AND v.story.id IS NULL THEN 1
                                       ELSE 0
                                     END) > 0 THEN TRUE
                            ELSE FALSE END
                        FROM Follow f
                        JOIN User   u ON u.id = f.followee.id
                        LEFT JOIN latest l
                               ON l.user_id = u.id AND l.rn <= 5
                        LEFT JOIN StoryView v
                               ON v.story.id = l.story_id
                              AND v.user.id = :currentUserId
                        WHERE f.follower.id = :currentUserId
                        GROUP BY u.id, u.username, u.profileImageUrl
                        HAVING COUNT(l.story_id) > 0
                        ORDER BY MAX(l.created_at) DESC, u.id DESC
                        """, Object[].class)
                .setParameter("currentUserId", currentUserId)
                .setFirstResult(page * 10)
                .setMaxResults(10)
                .getResultList();
    }

    public Long getStoriesCountFromFollowees(Integer currentUserId) {
        return em.createQuery("""
                        SELECT count(distinct u.id)
                        FROM Follow f
                        JOIN f.followee u
                        WHERE f.follower.id = :currentUserId
                          AND EXISTS (
                            SELECT 1
                            FROM Story s
                            WHERE s.user = u
                              AND s.status = 'ACTIVE'
                          )
                        """, Long.class)
                .setParameter("currentUserId", currentUserId)
                .getSingleResult();
    }

    public List<Object[]> findMyStories(Integer currentUserId) {
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
                            ORDER BY s.createdAt ASC, s.id ASC
                        """, Object[].class)
                .setParameter("currentUserId", currentUserId)
                .setParameter("status", StoryStatus.ACTIVE)
                .setMaxResults(5)
                .getResultList();

        return obsList;
    }

    public List<Object[]> findStoriesByUserId(Integer userId, Integer currentUserId) {
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
                           ORDER BY s.createdAt ASC, s.id ASC
                        """, Object[].class)
                .setParameter("userId", userId)
                .setParameter("currentUserId", currentUserId)
                .setParameter("status", StoryStatus.ACTIVE)
                .setMaxResults(5)
                .getResultList();

        return obsList;
    }
}
