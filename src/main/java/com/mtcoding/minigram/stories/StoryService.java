package com.mtcoding.minigram.stories;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoryService {
    private final StoryRepository storyRepository;

    public StoryResponse.DetailDTO findByStoryId(Integer storyId, Integer currentUserId) {
        Object[] objects = storyRepository.findByStoryId(storyId, currentUserId)
                .orElseThrow(()-> new ExceptionApi404("스토리를 찾을 수 없습니다"));

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

            Boolean isOwner = story.getUser().getId().equals(userId);

            return new StoryResponse.DetailDTO(story, isFollowing, isOwner, isLiked, likeCount);
        }).toList();

        return new StoryResponse.ListDTO(detailDTOList);
    }
}
