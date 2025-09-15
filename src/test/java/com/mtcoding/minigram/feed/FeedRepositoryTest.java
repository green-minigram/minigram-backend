package com.mtcoding.minigram.feed;

import com.mtcoding.minigram.stories.Story;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@Import(FeedRepository.class)
@DataJpaTest
public class FeedRepositoryTest {
    @Autowired
    private FeedRepository feedRepository;

    @Test
    public void findMyStories_test() {
        int currentUserId = 2;

        List<Object[]> obsList = feedRepository.findMyStories(currentUserId);
        System.out.println("=========== 내 스토리 목록 5개 조회 ============");
        for (Object[] ob : obsList) {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            int likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            System.out.println("storyId     : " + story.getId());
            System.out.println("ownerId     : " + story.getUser().getId());
            System.out.println("username    : " + story.getUser().getUsername());
            System.out.println("videoUrl    : " + story.getVideoUrl());
            System.out.println("isFollowing : " + isFollowing);
            System.out.println("likeCount   : " + likeCount);
            System.out.println("isLiked     : " + isLiked);
            System.out.println("------------------------------------------");
        }
        System.out.println("==========================================");
    }

    @Test
    public void findStoriesByUserId_test() {
        int currentUserId = 2;
        int userId = 3;

        List<Object[]> obsList = feedRepository.findStoriesByUserId(userId, currentUserId);
        System.out.println("=========== 다른 유저 스토리 목록 5개 조회 ============");
        for (Object[] ob : obsList) {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            int likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            System.out.println("storyId       : " + story.getId());
            System.out.println("ownerId       : " + story.getUser().getId());
            System.out.println("ownerUsername : " + story.getUser().getUsername());
            System.out.println("videoUrl      : " + story.getVideoUrl());
            System.out.println("isFollowing   : " + isFollowing + "  (로그인 유저=" + currentUserId + " → 대상 유저=" + userId + ")");
            System.out.println("likeCount     : " + likeCount);
            System.out.println("isLiked       : " + isLiked);
            System.out.println("------------------------------------------");
        }
        System.out.println("==========================================");
    }

    @Test
    public void findStoriesFromFollowees_test() {
        Integer currentUserId = 2;
        Integer page = 0;

        List<Object[]> obsList = feedRepository.findStoriesFromFollowees(page, currentUserId);
        System.out.println("=========== 팔로잉 유저의 최신 스토리 보유 여부 조회 ============");
        for (Object[] obs : obsList) {
            Integer followeeId = (Integer) obs[0];
            String username = (String) obs[1];
            String profileImageUrl = (String) obs[2];   // null 허용
            Boolean hasUnseen = (Boolean) obs[3];   // 내가 아직 안 본 활성 스토리가 하나라도 있으면 true

            System.out.println("followeeId     : " + followeeId);
            System.out.println("username       : " + username);
            System.out.println("profileImageUrl: " + profileImageUrl);
            System.out.println("hasUnseen    : " + hasUnseen + "  (viewer=" + currentUserId + ")");
            System.out.println("------------------------------------------");
        }
        System.out.println("==========================================");
    }
}
