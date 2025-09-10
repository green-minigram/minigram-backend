package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;

    //게시글 댓글 조회
    public CommentResponse.ListDTO findAllByPostId(Integer postId, Integer userId) {

        Integer postAuthorId = postRepository.findAuthorIdByPostId(postId);
        if (postAuthorId == null) throw new ExceptionApi404("게시글이 존재하지 않습니다"); // [CHANGED] 존재하지 않는 게시글 처리


        // 부모/자식 조회
        List<Comment> parents = commentRepository.findParentsByPostId(postId);
        if (parents.isEmpty()) return new CommentResponse.ListDTO(List.of());

        List<Integer> parentIds = parents.stream().map(Comment::getId).toList();
        List<Comment> children = commentRepository.findChildrenByParentIds(parentIds);

        // parentId -> children 매핑
        Map<Integer, List<Comment>> childrenMap = new LinkedHashMap<>();
        parentIds.forEach(id -> childrenMap.put(id, new ArrayList<>()));
        for (Comment child : children) {
            childrenMap.get(child.getParent().getId()).add(child);
        }

        // 전체 commentId 수집
        List<Integer> allIds = new ArrayList<>(parentIds.size() + children.size());
        allIds.addAll(parentIds);
        allIds.addAll(children.stream().map(Comment::getId).toList());

        // 좋아요 집계/뷰어 좋아요 (빈 목록 방어)
        Map<Integer, Integer> countMap = allIds.isEmpty()
                ? Map.of()
                : commentLikeRepository.countByCommentIds(allIds);
        Set<Integer> likedSet = (userId == null || allIds.isEmpty())
                ? Set.of()
                : commentLikeRepository.findLikedCommentIdsByUser(userId, allIds);

        // DTO 변환 + children 세팅
        List<CommentResponse.ItemDTO> items = parents.stream()
                .map(parent -> {
                    List<CommentResponse.ItemDTO> childDtos = childrenMap
                            .getOrDefault(parent.getId(), List.of())
                            .stream()
                            .map(child -> toItemWithLikes(child, userId, postAuthorId, countMap, likedSet, List.of()))
                            .toList();

                    return toItemWithLikes(parent, userId, postAuthorId, countMap, likedSet, childDtos);
                })
                .toList();

        log.info("comments.result parents={}, children={}, allIds={}, likedSetSize={}",
                items.size(), children.size(), allIds.size(), likedSet.size()); // 테스트 확인 로그 추가

        return new CommentResponse.ListDTO(items);
    }

    // 단일 댓글에 대해 likeCount/liked 계산 후 DTO로 변환하고 owner/postAuthor 플래그 설정
    private CommentResponse.ItemDTO toItemWithLikes(Comment comment, Integer userId, Integer postAuthorId, Map<Integer, Integer> countMap, Set<Integer> likedSet, List<CommentResponse.ItemDTO> childrenDtos) {
        int likeCount = countMap.getOrDefault(comment.getId(), 0);
        boolean liked = likedSet.contains(comment.getId());
        boolean owner = (userId != null) && userId.equals(comment.getUser().getId());
        boolean isPostAuthor = Objects.equals(comment.getUser().getId(), postAuthorId);

        return CommentResponse.ItemDTO.from(comment, (childrenDtos == null) ? List.of() : childrenDtos, likeCount, liked, owner, isPostAuthor);
    }

    @Transactional
    public CommentResponse.DeleteDTO delete(Integer commentId, Integer requesterId, String roles) {
        Comment comment = commentRepository.findWithPostAndUsersById(commentId)
                .orElseThrow(() -> new ExceptionApi404("댓글이 존재하지 않습니다."));

        boolean isAuthor = comment.getUser().getId().equals(requesterId);
        boolean isPostAuthor = comment.getPost().getUser().getId().equals(requesterId);
        boolean isAdmin = roles != null && roles.contains("ADMIN");

        if (!(isAuthor || isPostAuthor || isAdmin)) {
            throw new ExceptionApi403("삭제 권한이 없습니다.");
        }

        if (comment.isDeleted()) {
            return new CommentResponse.DeleteDTO(commentId, "이미 삭제된 댓글입니다.");
        }

        comment.markDeleted(); // 엔티티 도메인 메서드
        String msg = isAdmin
                ? "관리자 권한으로 댓글을 삭제했습니다."
                : isPostAuthor
                ? "게시글 작성자 권한으로 댓글을 삭제했습니다."
                : "댓글을 삭제했습니다.";

        return new CommentResponse.DeleteDTO(commentId, msg);
    }
}