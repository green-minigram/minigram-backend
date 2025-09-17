package com.mtcoding.minigram.stories.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class StoryLikeRepository {
    private final EntityManager em;

    public StoryLike save(StoryLike storyLike) {
        em.persist(storyLike);
        return storyLike;
    }

    public Long countByStoryId(Integer storyId) {
        return em.createQuery("""
                        SELECT COUNT(sl)
                        FROM StoryLike sl
                        WHERE sl.story.id = :storyId
                        """, Long.class)
                .setParameter("storyId", storyId)
                .getSingleResult();
    }

    public void deleteByStoryIdAndUserId(Integer storyId, Integer currentUserId) {
        em.createQuery("""
                            DELETE FROM StoryLike sl
                            WHERE sl.story.id = :storyId
                              AND sl.user.id = :currentUserId
                        """)
                .setParameter("storyId", storyId)
                .setParameter("currentUserId", currentUserId)
                .executeUpdate();
    }

    public boolean existsByStoryIdAndUserId(Integer storyId, Integer userId) {
        Long cnt = em.createQuery("""
                            select count(sl) from StoryLike sl
                            where sl.story.id = :sid and sl.user.id = :uid
                        """, Long.class)
                .setParameter("sid", storyId)
                .setParameter("uid", userId)
                .getSingleResult();
        return cnt > 0;
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
}
