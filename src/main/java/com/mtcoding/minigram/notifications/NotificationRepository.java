package com.mtcoding.minigram.notifications;

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
    public List<Notification> findRecentByRecipientId(Integer recipientUserId) {
        return em.createQuery("""
                            select n
                            from Notification n
                            join fetch n.sender s
                            where n.recipient.id = :rid
                            order by n.id desc
                        """, Notification.class)
                .setParameter("rid", recipientUserId)
                .setMaxResults(20)
                .getResultList();
    }

    // (옵션) 나중에 무한스크롤 붙일 때 쓸 커서 버전
    public List<Notification> findByRecipientIdWithCursor(Integer recipientUserId, Long cursorId, int size) {
        return em.createQuery("""
                            select n
                            from Notification n
                            join fetch n.sender s
                            where n.recipient.id = :rid
                              and (:cursor is null or n.id < :cursor)
                            order by n.id desc
                        """, Notification.class)
                .setParameter("rid", recipientUserId)
                .setParameter("cursor", cursorId)
                .setMaxResults(size)
                .getResultList();
    }
}
