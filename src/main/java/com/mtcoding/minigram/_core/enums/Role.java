package com.mtcoding.minigram._core.enums;

public enum Role {
    USER, ADMIN;

    public String asAuthority() {
        return "ROLE_" + name();
    }
}
