package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram.users.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationRepository {

    @PersistenceContext
    private EntityManager em;

    // 최신 20개만 (리스트 먼저 띄우기)
    public List<Notification> findRecent20ByRecipientId(Long recipientUserId) {
        String q = """
                    SELECT n FROM Notification n
                    JOIN FETCH n.sender s
                    WHERE n.recipient.id = :rid
                    ORDER BY n.id DESC
                """;
        return em.createQuery(q, Notification.class)
                .setParameter("rid", recipientUserId)
                .setMaxResults(20)
                .getResultList();
    }

    // (옵션) 나중에 무한스크롤 붙일 때 쓸 커서 버전
    public List<Notification> findByReceiverIdWithCursor(User recipient, Long cursorId, int size) {
        String q = """
                    SELECT n FROM Notification n
                    WHERE n.recipient = :rid
                      AND (:cursor IS NULL OR n.id < :cursor)
                    ORDER BY n.id DESC
                """;
        return em.createQuery(q, Notification.class)
                .setParameter("rid", recipient)
                .setParameter("cursor", cursorId)
                .setMaxResults(size)
                .getResultList();
    }
}
