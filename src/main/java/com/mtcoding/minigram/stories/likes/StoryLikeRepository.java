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
}
