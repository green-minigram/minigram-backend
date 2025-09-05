package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.follows.FollowRepository;
import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.likes.PostLikeRepository;
import com.mtcoding.minigram.reports.ReportRepository;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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


    // 게시글 상세
    public PostResponse.DetailDTO find(Integer postId, User sessionUser) {
//        Integer userId = (user == null) ? null : user.getId();

        // 1) 엔티티 로드
        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다."));

        List<PostImage> images = postRepository.findImagesByPostId(postId);

        // 2) 동적 값 계산
        int likeCount = (int) postLikeRepository.countByPostId(postId);

        boolean liked = sessionUser.getId() != null && postLikeRepository.existsByPostIdAndUserId(postId, sessionUser.getId());

        int commentCount = (int) commentRepository.countByPostId(postId);

        boolean owner = sessionUser.getId() != null && sessionUser.getId().equals(postPS.getUser().getId());

        // 팔로잉 여부
        boolean following = false;
        if (sessionUser.getId() != null && !owner) {
            following = followRepository.existsByFollowerIdAndFolloweeId(
                    sessionUser.getId(), postPS.getUser().getId()
            );
        }

        // 신고 여부
        boolean reported = sessionUser.getId() != null
                && reportRepository.existsActivePostReportByUser(postId, sessionUser.getId());


        log.info("[POST_FIND] out: likes(count={}, liked={}), comments={}, owner={}, following={}, reported={}",
                likeCount, liked, commentCount, owner, following, reported);

        // 3) 생성자 주입으로 한 번에 완성
        return new PostResponse.DetailDTO(postPS, images, likeCount, liked, commentCount, owner, following, reported);
    }
}
