package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.users.User;
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

    public StoryResponse.ListDTO findAllMyStories(Integer currentUserId) {
        List<Object[]> obsList = storyRepository.findAllMyStories(currentUserId);

        List<StoryResponse.DetailDTO> detailDTOList = obsList.stream().map(ob -> {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            Integer likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            boolean isOwner = true;

            return new StoryResponse.DetailDTO(story, isFollowing, isOwner, isLiked, likeCount);
        }).toList();

        return new StoryResponse.ListDTO(detailDTOList);
    }

    public StoryResponse.ListDTO findAllByUserId(Integer userId, Integer currentUserId) {
        List<Object[]> obsList = storyRepository.findAllByUserId(userId, currentUserId);

        List<StoryResponse.DetailDTO> detailDTOList = obsList.stream().map(ob -> {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            Integer likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            Boolean isOwner = story.getUser().getId().equals(currentUserId);

            return new StoryResponse.DetailDTO(story, isFollowing, isOwner, isLiked, likeCount);
        }).toList();

        return new StoryResponse.ListDTO(detailDTOList);
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

    public StoryResponse.FeedDTO getFeedStories(Integer page, Integer currentUserId) {
        // 1. userId, username, profileImageUrl, hasUnseen 조회
        List<Object[]> obsList = storyRepository.findFromFollowees(page, currentUserId);

        // 2. ItemDTO 조립
        List<StoryResponse.ItemDTO> itemDTOList = obsList.stream()
                .map(obs -> new StoryResponse.ItemDTO(
                        (Integer) obs[0],
                        (String) obs[1],
                        (String) obs[2],
                        (Boolean) obs[3]
                ))
                .toList();

        // 3. totalCount 조회
        Integer totalCount = Math.toIntExact(storyRepository.totalCountFromFollowees(currentUserId));

        return new StoryResponse.FeedDTO(itemDTOList, page, totalCount);
    }
}
