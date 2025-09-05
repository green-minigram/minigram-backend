package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.follows.FollowRepository;
import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.likes.PostLikeRepository;
import com.mtcoding.minigram.posts.likes.PostLikeResponse.LikesDTO;
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


    // 게시글 상세
    @Transactional(readOnly = true)
    public PostResponse.DetailDTO find(Integer postId, Integer viewerId) {
//        Integer userId = (user == null) ? null : user.getId();

        // 1) 엔티티 로드
        Post postPS = postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다."));

        List<PostImage> images = postRepository.findImagesByPostId(postId);

        // 2) 기본 DTO 구성
        PostResponse.DetailDTO dto = new PostResponse.DetailDTO(postPS, images);

        // 3) 좋아요 정보
        int likeCount = (int) postLikeRepository.countByPostId(postId);
        boolean liked = viewerId != null && postLikeRepository.existsByPostIdAndUserId(postId, viewerId);
        dto.setLikes(new LikesDTO(likeCount, liked));

        // 4) 댓글 개수
        int commentCount = (int) commentRepository.countByPostId(postId);
        dto.setCommentCount(commentCount);

        // 5) 소유자/팔로잉 여부 (AuthorDTO 내부 필드로 세팅)
        boolean owner = viewerId != null && viewerId.equals(postPS.getUser().getId());
        dto.getAuthor().setIsOwner(owner);

        // 팔로우 여부
        boolean following = false;
        if (viewerId != null && !owner) {
            following = followRepository.existsByFollowerIdAndFolloweeId(
                    viewerId, postPS.getUser().getId()
            );
        }
        dto.getAuthor().setIsFollowing(following);

        // 신고 여부
        boolean reported = viewerId != null
                && reportRepository.existsActivePostReportByUser(postId, viewerId);
        dto.setIsReported(reported);

        return dto;
    }
}
