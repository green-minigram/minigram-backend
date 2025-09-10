package com.mtcoding.minigram.posts.likes;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostLikeResponse.LikesDTO like(Integer postId, Integer userId) {
        var post = postRepository.findPostById(postId)
                .orElseThrow(() -> new ExceptionApi404("게시글이 존재하지 않습니다."));
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 이미 눌렀으면 현재 상태 반환
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            int count = (int) postLikeRepository.countByPostId(postId);
            return new PostLikeResponse.LikesDTO(count, true);
        }

        // 신규 좋아요
        try {
            postLikeRepository.save(new PostLike(post, user));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 동시성으로 인한 unique 충돌 → 이미 좋아요 처리
        }

        int count = (int) postLikeRepository.countByPostId(postId);
        return new PostLikeResponse.LikesDTO(count, true);
    }

    @Transactional
    public PostLikeResponse.LikesDTO unlike(Integer postId, Integer userId) {
        postRepository.findPostById(postId)
                .orElseThrow(() -> new ExceptionApi404("게시글이 존재하지 않습니다."));
        userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 있으면 삭제, 없으면 noop (멱등)
        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(postLikeRepository::delete);
        // (벌크로 하고 싶으면 deleteByPostIdAndUserId 사용)

        int count = (int) postLikeRepository.countByPostId(postId);
        return new PostLikeResponse.LikesDTO(count, false);
    }
}
