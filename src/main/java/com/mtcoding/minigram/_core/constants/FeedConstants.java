package com.mtcoding.minigram._core.constants;

public final class FeedConstants {
    public static final int ITEMS_PER_PAGE = 10; // 한 페이지 아이템 수
    public static final int AD_EVERY = 5; // 5번째마다 광고
    public static final int ADS_PER_PAGE = ITEMS_PER_PAGE / AD_EVERY; // 2
    public static final int POSTS_PER_PAGE = ITEMS_PER_PAGE - ADS_PER_PAGE; // 8

    // 한 페이지에서 광고 삽입 위치(0-based index)
    public static final int[] AD_SLOTS = {4, 9}; // 0~3 일반, 4 광고, 5~8 일반, 9 광고
}