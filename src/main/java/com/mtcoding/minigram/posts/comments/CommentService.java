package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.PostStatus;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import jakarta.validation.Valid;
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
    private final UserRepository userRepository;

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

    @Transactional
    public CommentResponse.DTO create(Integer postId, CommentRequest.CreateDTO reqDTO, Integer currentUserId) {
        // 1. 게시글 존재 + 상태 체크
        Post postPS =  postRepository.findById(postId)
                .orElseThrow(() -> new ExceptionApi404("존재하지 않는 게시글입니다"));

        if (postPS.getStatus() != PostStatus.ACTIVE) {
            throw new ExceptionApi403("현재 상태의 게시글에는 댓글을 작성할 수 없습니다.");
        }

        // 2. 로그인 사용자 FK만 필요 (userId)
        User userRef = userRepository.getReferenceById(currentUserId);

        // 3. 부모 및 루트 계산
        Comment parentPS = null;
        Comment root = null;

        // parentId가 있는 경우
        if (reqDTO.getParentId() != null) {
            parentPS = commentRepository.findById(reqDTO.getParentId())
                    .orElseThrow(() -> new ExceptionApi404("부모 댓글을 찾을 수 없습니다."));

            if (!parentPS.getPost().getId().equals(postPS.getId())) {
                throw new ExceptionApi400("부모 댓글과 게시글이 일치하지 않습니다.");
            }

            if (parentPS.getStatus() != CommentStatus.ACTIVE) {
                throw new ExceptionApi400("비활성화된 댓글에는 대댓글을 달 수 없습니다.");
            }

            // 부모가 최상위 댓글인 경우 → root == 부모
            // 부모가 이미 root에 속한 대댓글인 경우 → root == 그대로 부모의 root
            root = (parentPS.getRoot() == null) ? parentPS : parentPS.getRoot();
        }

        Comment commentPS = commentRepository.save(reqDTO.toEntity(postPS, root, parentPS, userRef));

        return new CommentResponse.DTO(commentPS);
    }
}