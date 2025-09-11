package com.mtcoding.minigram.reports.reasons;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReportReasonRepository {

    private fianl EntityManager em;

    public Optional<ReportReason> findById(Integer id) {
        return Optional.ofNullable(em.find(ReportReason.class, id));
    }
}
