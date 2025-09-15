package com.mtcoding.minigram.stories.likes;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.StoryRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoryLikeService {
    private final StoryLikeRepository storyLikeRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;

    @Transactional
    public StoryLikeResponse.CreateDTO create(Integer storyId, Integer currentUserId) {
        Story storyPS = storyRepository.findById(storyId)
                .orElseThrow(() -> new ExceptionApi404("스토리를 찾을 수 없습니다"));

        User userRef = userRepository.getReferenceById(currentUserId);

        StoryLike storyLike = StoryLike.builder()
                .story(storyPS)
                .user(userRef)
                .build();

        StoryLike storyLikePS = storyLikeRepository.save(storyLike);

        int likeCount = (storyLikeRepository.countByStoryId(storyId)).intValue();

        return new StoryLikeResponse.CreateDTO(storyLikePS, likeCount);
    }
}
