package com.mtcoding.minigram.advertisements;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Scheduled 작동을 위해 스케줄링 엔진 활성화(@EnableScheduling)
@Configuration
@EnableScheduling
public class SchedulingConfig {
}