package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.follows.FollowRepository;
import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.likes.PostLikeRepository;
import com.mtcoding.minigram.reports.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final ReportRepository reportRepository;

    @Transactional(readOnly = true)
    // 게시글 상세
    public PostResponse.DetailDTO find(Integer postId, Integer userId) {

        // 1) 엔티티 로드
        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다."));
        // 삭제된 글은 404
        if (postPS.getStatus() == PostStatus.DELETED) {
            throw new ExceptionApi404("존재하지 않는 게시글입니다.");
        }

        List<PostImage> images = postRepository.findImagesByPostId(postId);

        // 2) 동적 값 계산
        int likeCount = (int) postLikeRepository.countByPostId(postId);

        boolean liked = userId != null && postLikeRepository.existsByPostIdAndUserId(postId, userId);

        int commentCount = (int) commentRepository.countByPostId(postId);

        boolean owner = userId != null && userId.equals(postPS.getUser().getId());

        // 팔로잉 여부
        boolean following = false;
        if (userId != null && !owner) {
            following = followRepository.existsByFollowerIdAndFolloweeId(
                    userId, postPS.getUser().getId()
            );
        }

        // 신고 여부
        boolean reported = userId != null
                && reportRepository.existsActivePostReportByUser(postId, userId);


        log.info("[POST_FIND] out: likes(count={}, liked={}), comments={}, owner={}, following={}, reported={}",
                likeCount, liked, commentCount, owner, following, reported);

        // 3) 생성자 주입으로 한 번에 완성
        return new PostResponse.DetailDTO(postPS, images, likeCount, liked, commentCount, owner, following, reported);
    }

    @Transactional
    // 게시글 삭제(소프트)
    public PostResponse.DeleteDTO delete(Integer postId, Integer requesterId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다."));

        // 이미 삭제된 글이면 멱등 성공
        if (post.getStatus() == PostStatus.DELETED) {
            return new PostResponse.DeleteDTO(post.getId(), true);
        }

        // 권한 체크(소유자만)
        Integer ownerId = post.getUser().getId();
        if (!ownerId.equals(requesterId)) {
            throw new ExceptionApi403("본인 게시글만 삭제할 수 있습니다.");
        }

        post.setStatus(PostStatus.DELETED); // 소프트 삭제
        return new PostResponse.DeleteDTO(post.getId(), true);
    }

}
