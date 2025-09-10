package com.mtcoding.minigram.posts.likes;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.users.User;
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

    //좋아요 생성
    @Transactional
    public PostLikeResponse.LikesDTO like(Integer postId, Integer userId) {

        // 1) 존재 검증 (404)
        Post post = postRepository.findPostById(postId)
                .orElseThrow(() -> new ExceptionApi404("게시글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 2) 이미 좋아요면 멱등 처리: 카운트만 갱신해서 반환
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            int count = (int) postLikeRepository.countByPostId(postId);
            return new PostLikeResponse.LikesDTO(count, true);
        }

        // 3) 신규 좋아요 (동시 삽입 경합은 UNIQUE 제약으로 방지)
        try {
            postLikeRepository.save(new PostLike(post, user));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 레이스 컨디션: 동시에 같은 (postId,userId) 삽입 → 이미 좋아요로 간주
        }

        // 4) 최종 카운트 재집계 후 isLiked=true 반환
        int count = (int) postLikeRepository.countByPostId(postId);
        return new PostLikeResponse.LikesDTO(count, true);
    }

    //좋아요 취소
    @Transactional
    public PostLikeResponse.LikesDTO unlike(Integer postId, Integer userId) {

        // 1) 존재 검증 (404)
        postRepository.findPostById(postId)
                .orElseThrow(() -> new ExceptionApi404("게시글이 존재하지 않습니다."));
        userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 2) 있으면 삭제, 없으면 NOOP (멱등)
        postLikeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(postLikeRepository::delete);

        // 3) 최종 카운트 재집계 후 isLiked=false 반환
        int count = (int) postLikeRepository.countByPostId(postId);
        return new PostLikeResponse.LikesDTO(count, false);
    }
}
