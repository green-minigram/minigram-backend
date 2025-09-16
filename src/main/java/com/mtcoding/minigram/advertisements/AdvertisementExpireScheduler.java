package com.mtcoding.minigram.advertisements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdvertisementExpireScheduler {

    private final AdvertisementService advertisementService;

    // 시작 2초 후부터 30초 간격 (프로퍼티로도 조정 가능)
    @Scheduled(
            initialDelayString = "${app.ads.expire.initial-delay:2000}",
            fixedDelayString = "${app.ads.expire.fixed-delay:30000}"
    )


    public void run() {
        // 스케줄러는 서비스의 expire()만 호출 — DB 변경은 서비스에서 트랜잭션으로 처리
        int updated = advertisementService.expire();
        log.info("[광고만료 스케줄] 실행됨, 상태 변경된 광고 수={}", updated);
    }
}