package com.mtcoding.minigram.notifications;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationRepository {
    private final EntityManager em;

    public List<Object[]> findTop20ByRecipientId(Integer userId) {
        return em.createQuery("""
                        SELECT
                          n,
                          CASE WHEN EXISTS (
                            SELECT 1 FROM Follow f
                            WHERE f.follower.id = :recipientId
                              AND f.followee = s
                          ) THEN true ELSE false END
                        FROM Notification n
                        JOIN FETCH n.sender s
                        WHERE n.recipient.id = :recipientId
                        ORDER BY n.createdAt DESC, n.id DESC
                        """, Object[].class)
                .setParameter("recipientId", userId)
                .setMaxResults(20)
                .getResultList();
    }
}
