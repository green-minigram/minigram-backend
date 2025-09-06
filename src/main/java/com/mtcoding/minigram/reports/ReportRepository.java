package com.mtcoding.minigram.reports;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ReportRepository {
    private final EntityManager em;

    // 뷰어가 특정 게시글을 신고한 "유효한" 이력이 있는지 (PENDING/APPROVED)
    public boolean existsActivePostReportByUser(Integer postId, Integer userId) {
        Long cnt = em.createQuery(
                        "select count(r) from Report r " +
                                "where r.type = :type and r.targetId = :targetId " +
                                "and r.reporter.id = :userId and r.status in (:active)",
                        Long.class
                )
                .setParameter("type", ReportType.POST) // 타입이 문자열이면 "POST" 사용
                .setParameter("targetId", postId)
                .setParameter("userId", userId)
                .setParameter("active", List.of(ReportStatus.PENDING, ReportStatus.APPROVED))
                .getSingleResult();

        return cnt != null && cnt > 0;
    }
}
