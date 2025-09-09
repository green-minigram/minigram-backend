package com.mtcoding.minigram.follows;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponse.DTO create(Integer currentUserId, Integer followeeId) {
        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new ExceptionApi404("팔로우 대상 유저를 찾을 수 없습니다"));

        if (currentUserId.equals(followee.getId())) {
            throw new ExceptionApi400("자기 자신은 팔로우할 수 없습니다");
        }

        boolean exists = followRepository.existsByFollowerIdAndFolloweeId(currentUserId, followee.getId());
        if (exists) throw new ExceptionApi400("이미 팔로우 중입니다");

        User follower = userRepository.getReferenceById(currentUserId);

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        try {
            Follow followPS = followRepository.save(follow);
            return new FollowResponse.DTO(followPS);
        } catch (DataIntegrityViolationException e) {
            // (follower_id, followee_id) 유니크 제약 위반 등
            throw new ExceptionApi400("이미 팔로우 중입니다");
        }
    }

    @Transactional
    public FollowResponse.DeleteDTO delete(Integer currentUserId, Integer followeeId) {
        Follow followPS = followRepository.findByFollowerIdAndFolloweeId(currentUserId, followeeId)
                .orElseThrow(() -> new ExceptionApi404("팔로우 내역을 찾을 수 없습니다"));

        if (!followPS.getFollower().getId().equals(currentUserId)) {
            throw new ExceptionApi403("권한이 없습니다");
        }

        followRepository.deleteById(followPS.getId());

        return new FollowResponse.DeleteDTO(followeeId);
    }

    public FollowResponse.ListDTO findFollowers(Integer currentUserId, Integer followeeId) {
        List<Object[]> obsList = followRepository.findFollowersByFolloweeId(currentUserId, followeeId);

        List<FollowResponse.ItemDTO> itemDTOList = obsList.stream().map(ob -> {
            User user = (User) ob[0];
            Boolean isFollowing = (Boolean) ob[1];

            Boolean isMe = user.getId().equals(currentUserId);

            return new FollowResponse.ItemDTO(user, isFollowing, isMe);
        }).toList();

        return new FollowResponse.ListDTO(itemDTOList);
    }

    public FollowResponse.ListDTO findFollowing(Integer currentUserId, Integer followerId) {
        List<Object[]> obsList = followRepository.findFollowingByFollowerId(currentUserId, followerId);

        List<FollowResponse.ItemDTO> itemDTOList = obsList.stream().map(ob -> {
            User user = (User) ob[0];
            Boolean isFollowing = (Boolean) ob[1];

            Boolean isMe = user.getId().equals(currentUserId);

            return new FollowResponse.ItemDTO(user, isFollowing, isMe);
        }).toList();

        return new FollowResponse.ListDTO(itemDTOList);
    }
}
