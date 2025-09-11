package com.mtcoding.minigram.stories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@Import(StoryRepository.class)
@DataJpaTest
public class StoryRepositoryTest {
    @Autowired
    private StoryRepository storyRepository;

    @Test
    public void findByStoryId_test() {
        int storyId = 1;
        int currentUserId = 2;

        Optional<Object[]> opt = storyRepository.findByStoryId(storyId, currentUserId);
        System.out.println("===========스토리 1건 조회============");
        if (opt.isPresent()) {
            Object[] objects = opt.get();

            Story story = (Story) objects[0];
            Boolean isFollowing = (Boolean) objects[1];
            int likeCount = ((Long) objects[2]).intValue();
            Boolean isLiked = (Boolean) objects[3];

            System.out.println("=========== 스토리 1건 조회 ===========");
            System.out.println("storyId      : " + story.getId());
            System.out.println("ownerId      : " + story.getUser().getId());
            System.out.println("videoUrl      : " + story.getVideoUrl());
            System.out.println("username     : " + story.getUser().getUsername());
            System.out.println("isFollowing  : " + isFollowing);
            System.out.println("likeCount    : " + likeCount);
            System.out.println("isLiked      : " + isLiked);
            System.out.println("======================================");
        } else {
            System.out.println("스토리를 찾을 수 없습니다.");
        }
    }

    @Test
    public void findAllMyStories_test() {
        int currentUserId = 2;

        List<Object[]> obsList = storyRepository.findAllMyStories(currentUserId);
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
    public void findAllByUserId_test() {
        int currentUserId = 2;
        int userId = 3;

        List<Object[]> obsList = storyRepository.findAllByUserId(userId, currentUserId);
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
    public void findFromFollowees_test() {
        Integer currentUserId = 2;
        Integer page = 0;

        List<Object[]> obsList = storyRepository.findFromFollowees(page, currentUserId);
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