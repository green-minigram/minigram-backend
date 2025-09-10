package com.mtcoding.minigram.posts.comments.likes;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.comments.Comment;
import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentLikeResponse.LikesDTO like(Integer commentId, Integer userId) {


        Comment comment = commentRepository.findCommentById(commentId)
                .orElseThrow(() -> new ExceptionApi404("댓글이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 이미 눌렀으면 멱등 반환
        var exists = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);
        if (exists.isPresent()) {
            int count = commentLikeRepository.countByCommentId(commentId);
            return new CommentLikeResponse.LikesDTO(count, true);
        }

        // 신규 저장 (빌더 사용)
        try {
            commentLikeRepository.save(
                    CommentLike.builder()
                            .comment(comment)
                            .user(user)
                            .build()
            );
        } catch (DataIntegrityViolationException e) {
            // 동시성으로 unique 충돌 시 무시하고 다음 단계로
        }

        int count = commentLikeRepository.countByCommentId(commentId);

        return new CommentLikeResponse.LikesDTO(count, true);
    }

    @Transactional
    public CommentLikeResponse.LikesDTO unlike(Integer commentId, Integer userId) {


        commentRepository.findCommentById(commentId)
                .orElseThrow(() -> new ExceptionApi404("댓글이 존재하지 않습니다."));
        userRepository.findById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 있으면 삭제, 없으면 noop (멱등)
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);

        int count = commentLikeRepository.countByCommentId(commentId);


        return new CommentLikeResponse.LikesDTO(count, false);
    }
}
