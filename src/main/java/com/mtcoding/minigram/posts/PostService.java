package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.likes.PostLikeRepository;
import com.mtcoding.minigram.posts.likes.PostLikeResponse.LikesDTO;
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
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;


    @Transactional(readOnly = true)
    public PostResponse.DetailDTO find(Integer postId, Integer viewerId) {
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));
        var images = postRepository.findImagesByPostId(postId);
        var dto = new PostResponse.DetailDTO(post, images);

        int likeCount = (int) postLikeRepository.countByPostId(postId);
        boolean liked = viewerId != null && postLikeRepository.existsByPostIdAndUserId(postId, viewerId);
        dto.setLikes(new LikesDTO(likeCount, liked));

        int commentCount = (int) commentRepository.countByPostId(postId);
        dto.setCommentCount(commentCount);

        boolean isOwner = viewerId != null && viewerId.equals(post.getUser().getId());
        dto.setIsOwner(isOwner);

//        boolean isFollowing = false;
//        if (viewerId != null && !isOwner) {
//            isFollowing = followRepository.existsByFollowerIdAndFolloweeId(viewerId, post.getAuthor().getId());
//        }
//        dto.setIsFollowing(isFollowing);

        return dto;
    }
}
