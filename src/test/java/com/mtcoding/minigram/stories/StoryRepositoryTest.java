package com.mtcoding.minigram.stories;

import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

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
    public void findMyStories_test() {
        int currentUserId = 2;

        List<Object[]> obsList = storyRepository.findMyStories(currentUserId);
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
}