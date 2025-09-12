package com.mtcoding.minigram.advertisements;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.mtcoding.minigram.posts.PostStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Advertisement save(Advertisement ad) {
        em.persist(ad);
        return ad;
    }

    public List<Object[]> findAllValid(int page, int currentUserId, LocalDateTime now, int totalCount) {
        if (totalCount <= 0) return List.of();

        final int adsPerPage = FeedConstants.ADS_PER_PAGE; // 페이지당 광고 수 (2개)
        final int start = (page * adsPerPage) % totalCount; // 이번 페이지의 광고 시작 인덱스 (totalCount를 넘어가면 0부터 다시 순환)

        String sql = """
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
                ORDER BY a.createdAt DESC, a.id DESC
                """;

        // 1. tail: start부터 끝까지 연속 구간에서 최대 adsPerPage개 조회
        List<Object[]> tail = em.createQuery(sql, Object[].class)
                .setParameter("adActive", AdvertisementStatus.ACTIVE)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("now", now)
                .setParameter("currentUserId", currentUserId)
                .setFirstResult(start)
                .setMaxResults(adsPerPage)
                .getResultList();

        if (tail.size() == adsPerPage) return tail;

        // 2. tail만으로 페이지당 광고 수 다 안채워 지면 앞에서부터 채워 넣기
        int fillCount = adsPerPage - tail.size(); // head에서 추가로 가져올 개수
        if (fillCount <= 0) return tail;

        List<Object[]> head = em.createQuery(sql, Object[].class)
                .setParameter("adActive", AdvertisementStatus.ACTIVE)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("now", now)
                .setParameter("currentUserId", currentUserId)
                .setFirstResult(0)
                .setMaxResults(fillCount)
                .getResultList();

        if (head.isEmpty()) return tail;

        List<Object[]> adList = new ArrayList<>(adsPerPage);
        adList.addAll(tail);
        adList.addAll(head);

        return adList;
    }

    public Long totalCount(LocalDateTime now) {
        return em.createQuery("""
                        SELECT COUNT(a.id)
                        FROM Advertisement a
                        JOIN a.post p
                        WHERE a.status = :adActive
                          AND a.startAt <= :now AND a.endAt >= :now
                          AND p.status = :postActive
                        """, Long.class)
                .setParameter("adActive", AdvertisementStatus.ACTIVE)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("now", now)
                .getSingleResult();
    }
}
