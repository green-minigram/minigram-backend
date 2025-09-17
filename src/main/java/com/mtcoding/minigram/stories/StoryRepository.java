package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.constants.UserDetailConstants;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StoryRepository {
    private final EntityManager em;

    public Optional<Object[]> findByStoryIdForUser(Integer storyId, Integer currentUserId) {
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

    public Story save(Story story) {
        em.persist(story);
        return story;
    }

    public Optional<Story> findById(Integer storyId) {
        return Optional.ofNullable(em.find(Story.class, storyId));
    }

    public boolean existsById(Integer targerId) {
        List<Integer> result = em.createQuery("""
                        select 1 from Story s where s.id = :stroyId
                        """, Integer.class)
                .setParameter("stroyId", targerId)
                .setMaxResults(1)
                .getResultList();
        return !result.isEmpty();
    }

    public List<Object[]> findAllByUserId(Integer profileUserId, boolean isOwner, Integer page) {
        return em.createQuery("""
                        SELECT s.id, s.thumbnailUrl
                        FROM Story s
                        WHERE s.user.id = :profileUserId
                          AND (
                               s.status = :storyActive
                               OR (:isOwner = true AND s.status = :storyHidden)
                          )
                        ORDER BY s.createdAt DESC, s.id DESC
                        """, Object[].class)
                .setParameter("profileUserId", profileUserId)
                .setParameter("isOwner", isOwner)
                .setParameter("storyActive", StoryStatus.ACTIVE)
                .setParameter("storyHidden", StoryStatus.HIDDEN)
                .setFirstResult(page * UserDetailConstants.ITEMS_PER_PAGE)
                .setMaxResults(UserDetailConstants.ITEMS_PER_PAGE)
                .getResultList();
    }

    public Long countAllByUserId(Integer profileUserId, boolean isOwner) {
        return em.createQuery("""
                        SELECT COUNT(s.id)
                        FROM Story s
                        WHERE s.user.id = :profileUserId
                          AND (
                               s.status = :storyActive
                               OR (:isOwner = true AND s.status = :storyHidden)
                          )
                        """, Long.class)
                .setParameter("profileUserId", profileUserId)
                .setParameter("isOwner", isOwner)
                .setParameter("storyActive", StoryStatus.ACTIVE)
                .setParameter("storyHidden", StoryStatus.HIDDEN)
                .getSingleResult();
    }

    // 작성자 포함 단건
    public Optional<Story> findWithAuthorById(Integer storyId) {
        List<Story> rows = em.createQuery("""
                            select s
                            from Story s
                            join fetch s.user a
                            where s.id = :id
                        """, Story.class)
                .setParameter("id", storyId)
                .getResultList();
        return rows.stream().findFirst();
    }

    // 좋아요 수
    public int countLikesByStoryId(Integer storyId) {
        Long cnt = em.createQuery("""
                            select count(l)
                            from StoryLike l
                            where l.story.id = :id
                        """, Long.class)
                .setParameter("id", storyId)
                .getSingleResult();
        return cnt.intValue();
    }

    public int countCommentsByStoryIdOrZero(Integer storyId) {
        return 0;
    }
}
