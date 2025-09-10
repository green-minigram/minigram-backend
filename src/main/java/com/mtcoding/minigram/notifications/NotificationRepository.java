package com.mtcoding.minigram.notifications;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class NotificationRepository {
    private final EntityManager em;

    public List<Object[]> findAllByRecipientIdWithinOneMonth(Integer userId) {
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
                          AND n.createdAt >= :oneMonthAgo
                        ORDER BY n.createdAt DESC, n.id DESC
                        """, Object[].class)
                .setParameter("recipientId", userId)
                .setParameter("oneMonthAgo", LocalDateTime.now().minusMonths(1))
                .getResultList();
    }

    // targetId == postLikeId 추가 정보 조회 (post.id, post_image.url)
    public List<Object[]> findPostLikeTargetDetailsByIds(List<Integer> postLikeIdList) {
        if (postLikeIdList == null || postLikeIdList.isEmpty()) return Collections.emptyList();

        return em.createQuery("""
                        SELECT
                          pl.id,
                          p.id,
                          pi.url
                        FROM PostLike pl
                        JOIN pl.post p
                        LEFT JOIN PostImage pi
                               ON pi.post = p
                              AND pi.id = (
                                   SELECT MIN(pi2.id)
                                   FROM PostImage pi2
                                   WHERE pi2.post = p
                              )
                        WHERE pl.id IN :ids
                        """, Object[].class)
                .setParameter("ids", postLikeIdList)
                .getResultList();
    }

    // targetId == commentId 추가 정보 조회 (post.id, post_image.url, commentContent)
    public List<Object[]> findCommentTargetDetailsByIds(List<Integer> commentIdList) {
        if (commentIdList == null || commentIdList.isEmpty()) return Collections.emptyList();

        return em.createQuery("""
                        SELECT
                          c.id,
                          p.id,
                          pi.url,
                          c.content
                        FROM Comment c
                        JOIN c.post p
                        LEFT JOIN PostImage pi
                               ON pi.post = p
                              AND pi.id = (
                                   SELECT MIN(pi2.id)
                                   FROM PostImage pi2
                                   WHERE pi2.post = p
                              )
                        WHERE c.id IN :ids
                        """, Object[].class)
                .setParameter("ids", commentIdList)
                .getResultList();
    }
}
