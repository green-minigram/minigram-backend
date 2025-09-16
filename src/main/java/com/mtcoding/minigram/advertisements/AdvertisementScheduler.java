package com.mtcoding.minigram.advertisements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdvertisementScheduler {

    private final AdvertisementRepository adRepo;

    @Scheduled(cron = "0 * * * * *") // 매 1분(초=0)
    @Transactional
    public void expireAds() {
        int updated = adRepo.updateExpiredToInactive(LocalDateTime.now());
        log.info("[AD_EXPIRE] expired->inactive updated={}", updated);
    }
}