package com.mtcoding.minigram.follows;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowRepository {
    private final EntityManager em;
}
