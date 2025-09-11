package com.mtcoding.minigram.advertisements;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class AdvertisementRepository {
    private final EntityManager em;

    public Advertisement save(Advertisement ad) {
        em.persist(ad);
        return ad;
    }
}
