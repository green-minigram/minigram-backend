package com.mtcoding.minigram.reports.reasons;

import lombok.Getter;

@Getter
public enum ReportReasonCode {
    DISLIKE("마음에 들지 않습니다"),
    BULLYING("따돌림 또는 원치 않는 연락"),
    SELF_HARM("자살, 자해 및 섭식 장애"),
    VIOLENCE("폭력, 혐오 또는 학대"),
    ILLEGAL_SALE("규제 품목을 판매하거나 홍보함"),
    NUDITY("나체 이미지 또는 성적 행위"),
    SPAM_SCAM("스팸, 사기 또는 스팸"),
    FALSE_INFO("거짓 정보"),
    IP_INFRINGEMENT("지식재산권 침해");

    private final String label;

    ReportReasonCode(String label) {
        this.label = label;
    }
}
