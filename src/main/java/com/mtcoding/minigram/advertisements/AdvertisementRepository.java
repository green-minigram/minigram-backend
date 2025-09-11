package com.mtcoding.minigram.advertisements;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class AdvertisementRepository {
    private final EntityManager em;

    public Optional<Advertisement> findActiveNowByPostId(Integer postId) {
        LocalDateTime now = LocalDateTime.now();
        List<Advertisement> list = em.createQuery(
                        "select a from Advertisement a " +
                                "where a.post.id = :postId " +
                                "  and a.status = :status " +
                                "  and :now between a.startAt and a.endAt",
                        Advertisement.class)
                .setParameter("postId", postId)
                .setParameter("status", AdvertisementStatus.ACTIVE)
                .setParameter("now", now)
                .setMaxResults(1)
                .getResultList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }
}
