package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.PostStatus;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    //게시글 댓글 조회
    public CommentResponse.ListDTO findAllByPostId(Integer postId, Integer currentUserId) {
        Post postPS =  postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다"));

        switch (postPS.getStatus()) {
            case DELETED -> throw new ExceptionApi404("삭제된 게시글입니다: postId=" + postId);
            case HIDDEN  -> throw new ExceptionApi404("숨김 처리된 게시글입니다: postId=" + postId);
        }

        List<Object[]> obsList = commentRepository.findAllByPostId(postId, currentUserId);

        List<CommentResponse.ItemDTO> itemDTOList = obsList.stream()
                .map(obs -> {
                    Comment comment = (Comment) obs[0];
                    boolean isLiked = Boolean.TRUE.equals(obs[1]);
                    int likeCount   = ((Number) obs[2]).intValue();
                    boolean hasUnseen = Boolean.TRUE.equals(obs[3]);
                    boolean isOwner   = Objects.equals(comment.getUser().getId(), currentUserId);

                    return new CommentResponse.ItemDTO(
                            comment,
                            isOwner,
                            isLiked,
                            likeCount,
                            hasUnseen
                    );
                })
                .toList();

        return new CommentResponse.ListDTO(itemDTOList);
    }

    @Transactional
    public CommentResponse.DeleteDTO delete(Integer commentId, Integer requesterId, String roles) {
        Comment comment = commentRepository.findWithPostAndUsersById(commentId)
                .orElseThrow(() -> new ExceptionApi404("댓글이 존재하지 않습니다."));

        boolean isAuthor = comment.getUser().getId().equals(requesterId);
        boolean isAdmin = roles != null && roles.contains("ADMIN");

        if (!(isAuthor || isAdmin)) {
            throw new ExceptionApi403("삭제 권한이 없습니다.");
        }

        if (comment.isDeleted()) {
            return new CommentResponse.DeleteDTO(commentId, "이미 삭제된 댓글입니다.");
        }

        comment.markDeleted(); // 엔티티 도메인 메서드
        String msg = isAdmin ? "관리자 권한으로 댓글을 삭제했습니다." : "댓글을 삭제했습니다.";
        return new CommentResponse.DeleteDTO(commentId, msg);
    }

    public CommentResponse.ListDTO findRepliesByRoot(Integer rootId, Integer currentUserId) {
        Comment root = commentRepository.findById(rootId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 댓글입니다: id=" + rootId));

        if (root.getParent() != null) {
            throw new ExceptionApi400("최상위 댓글이 아닙니다: id=" + rootId);
        }

        List<Object[]> obsList = commentRepository.findRepliesByRoot(rootId, currentUserId);

        List<CommentResponse.ItemDTO> itemDTOList = obsList.stream()
                .map(obs -> {
                    Comment comment = (Comment) obs[0];
                    boolean isLiked = Boolean.TRUE.equals(obs[1]);
                    int likeCount   = ((Number) obs[2]).intValue();
                    boolean hasUnseen = Boolean.TRUE.equals(obs[3]);
                    boolean isOwner   = Objects.equals(comment.getUser().getId(), currentUserId);

                    return new CommentResponse.ItemDTO(
                            comment,
                            isOwner,
                            isLiked,
                            likeCount,
                            hasUnseen
                    );
                })
                .toList();

        return new CommentResponse.ListDTO(itemDTOList);
    }
}