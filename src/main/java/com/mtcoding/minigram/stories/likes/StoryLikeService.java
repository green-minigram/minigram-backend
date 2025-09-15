package com.mtcoding.minigram.stories.likes;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.notifications.NotificationService;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.StoryRepository;
import com.mtcoding.minigram.stories.StoryStatus;
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
    private final NotificationService notificationService;

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

        // 알림
        notificationService.notifyStoryLiked(storyPS, storyLikePS, userRef);

        return new StoryLikeResponse.CreateDTO(storyLikePS, likeCount);
    }

    @Transactional
    public StoryLikeResponse.DeleteDTO delete(Integer storyId, Integer currentUserId) {
        Story storyPS = storyRepository.findById(storyId)
                .orElseThrow(() -> new ExceptionApi404("스토리를 찾을 수 없습니다"));

        if (storyPS.getStatus() != StoryStatus.ACTIVE) {
            throw new ExceptionApi404("비활성화된 스토리입니다.");
        }

        storyLikeRepository.deleteByStoryIdAndUserId(storyId, currentUserId);

        int likeCount = (storyLikeRepository.countByStoryId(storyId)).intValue();

        return new StoryLikeResponse.DeleteDTO(storyId, likeCount);
    }
}
