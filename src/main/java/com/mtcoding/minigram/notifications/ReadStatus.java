package com.mtcoding.minigram.notifications;

public enum ReadStatus {
    UNREAD("안읽음"),
    READ("읽음");

    private final String label;

    ReadStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
