package com.mtcoding.minigram.advertisements;

import com.mtcoding.minigram.posts.PostStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class AdvertisementRepository {
    private final EntityManager em;

    public List<Object[]> findAllValid(int page, int currentUserId, LocalDateTime now) {
        return em.createQuery("""
                        SELECT a,
                               (SELECT COUNT(pl1.id) FROM PostLike pl1 WHERE pl1.post = p),
                               CASE WHEN EXISTS (
                                   SELECT 1 FROM PostLike pl2
                                    WHERE pl2.post = p AND pl2.user.id = :currentUserId
                               ) THEN true ELSE false END,
                               (SELECT COUNT(c1.id) FROM Comment c1 WHERE c1.post = p)
                        FROM Advertisement a
                        JOIN FETCH a.post p
                        JOIN FETCH a.user u
                        WHERE a.status = :adActive
                          AND a.startAt <= :now AND a.endAt >= :now
                          AND p.status = :postActive
                        ORDER BY FUNCTION('RAND')
                        """, Object[].class)
                .setParameter("adActive", AdvertisementStatus.ACTIVE)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("now", now)
                .setParameter("currentUserId", currentUserId)
                .setFirstResult(page * 2)
                .setMaxResults(2)
                .getResultList();
    }
}
