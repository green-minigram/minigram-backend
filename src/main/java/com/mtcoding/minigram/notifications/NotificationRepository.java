package com.mtcoding.minigram.notifications;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class NotificationRepository {
    private final EntityManager em;
}
