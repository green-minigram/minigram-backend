package com.mtcoding.minigram.stories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

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
}