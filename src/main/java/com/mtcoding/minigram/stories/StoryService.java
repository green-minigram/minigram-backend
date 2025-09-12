package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryResponse.DetailDTO findByStoryId(Integer storyId, Integer currentUserId) {
        Object[] objects = storyRepository.findByStoryId(storyId, currentUserId)
                .orElseThrow(() -> new ExceptionApi404("스토리를 찾을 수 없습니다"));

        Story story = (Story) objects[0];
        Boolean isFollowing = (Boolean) objects[1];
        Integer likeCount = ((Long) objects[2]).intValue();
        Boolean isLiked = (Boolean) objects[3];

        Boolean isOwner = story.getUser().getId().equals(currentUserId);

        return new StoryResponse.DetailDTO(
                story,
                isFollowing,
                isOwner,
                isLiked,
                likeCount
        );
    }

    @Transactional
    public StoryResponse.DTO create(StoryRequest.CreateDTO reqDTO, User user) {
        Story story = reqDTO.toEntity(user);
        Story storyPS = storyRepository.save(story);
        // TODO : 썸네일 저장 로직 (비동기 처리?)
        return new StoryResponse.DTO(storyPS);
    }

    @Transactional
    public StoryResponse.DTO delete(Integer storyId, Integer currentUserId) {
        Story storyPS = storyRepository.findById(storyId)
                .orElseThrow(() -> new ExceptionApi404("스토리를 찾을 수 없습니다"));

        if (storyPS.getStatus() == StoryStatus.DELETED) {
            throw new ExceptionApi404("이미 삭제된 스토리입니다");
        }

        if (!storyPS.getUser().getId().equals(currentUserId)) {
            throw new ExceptionApi403("권한이 없습니다");
        }

        storyPS.delete();

        return new StoryResponse.DTO(storyPS);
    }

    public UserResponse.StoryListDTO getUserStories(Integer userId, Integer currentUserId, Integer page) {
        // 1. userId 없으면 내 프로필
        Integer profileUserId = (userId == null) ? currentUserId : userId;

        // 2. 본인 여부 판단
        boolean isOwner = profileUserId.equals(currentUserId);

        // 3. storyId, thumbnailUrl 조회
        List<Object[]> obsList = storyRepository.findAllByUserId(profileUserId, isOwner, page);

        // 4. StoryItemDTO 조립
        List<UserResponse.StoryItemDTO> storyItemList = obsList.stream()
                .map(obs -> new UserResponse.StoryItemDTO(
                        (Integer) obs[0],
                        (String) obs[1]
                ))
                .toList();

        // 5. totalCount 조회
        int totalCount = Math.toIntExact(storyRepository.countAllByUserId(profileUserId, isOwner));

        return new UserResponse.StoryListDTO(storyItemList, page, totalCount);
    }
}
