package com.mtcoding.minigram.stories;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class StoryRepository {
    private final EntityManager em;

    public void findByStoryId(Integer storyId) {
        List<Object[]> obs = em.createQuery("""
                        select s,
                               (select count(sl) from StoryLike sl where sl.story = s),
                               (case when exists (
                                   select 1 from StoryLike sl2
                                   where sl2.story = s and sl2.user.id = :viewerId
                               ) then true else false end),
                               (case when exists (
                                   select 1 from Follow f
                                   where f.follower.id = :viewerId and f.followee = a
                               ) then true else false end)
                        from Story s
                        join fetch s.author a
                        where s.id = :storyId
                        """, Object[].class)
                .setParameter("storyId", storyId)
                .getResultList();
    }
}
