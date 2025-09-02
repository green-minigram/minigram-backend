package com.mtcoding.minigram.reports;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReportRepository {
    private final EntityManager em;
}
