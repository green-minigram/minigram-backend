package com.mtcoding.minigram.follows;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.StoryResponse;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponse.DTO create(FollowRequest.CreateDTO reqDTO, User user) {
        User followee = userRepository.findById(reqDTO.getFolloweeId())
                .orElseThrow(() -> new ExceptionApi404("팔로우 대상 유저를 찾을 수 없습니다"));

        if (user.getId().equals(followee.getId())) {
            throw new ExceptionApi400("자기 자신은 팔로우할 수 없습니다");
        }

        boolean exists = followRepository.existsByFollowerIdAndFolloweeId(user.getId(), followee.getId());
        if (exists) throw new ExceptionApi400("이미 팔로우 중입니다");

        Follow follow = reqDTO.toEntity(user, followee);
        Follow followPS = followRepository.save(follow);
        return new FollowResponse.DTO(followPS);
    }
}
