package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeResponse;
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

    @Transactional(readOnly = true)
    public List<CommentResponse.ItemDTO> getCommentsByPostId(Integer postId, Integer viewerId) {

        Integer postAuthorId = postRepository.findAuthorIdByPostId(postId);

        // 부모/자식 조회 (네 기존 로직)
        List<Comment> parents = commentRepository.findParentsByPostId(postId);
        if (parents.isEmpty()) return List.of();

        List<Integer> parentIds = parents.stream().map(Comment::getId).toList();
        List<Comment> children = commentRepository.findChildrenByParentIds(parentIds);

        // parentId -> children 매핑
        Map<Integer, List<Comment>> childrenMap = new LinkedHashMap<>();
        parentIds.forEach(id -> childrenMap.put(id, new ArrayList<>()));
        for (Comment ch : children) childrenMap.get(ch.getParent().getId()).add(ch);

        // 전체 commentId 수집
        List<Integer> allIds = new ArrayList<>(parentIds);
        allIds.addAll(children.stream().map(Comment::getId).toList());

        // 좋아요 “조회만” (2쿼리)
        Map<Integer, Integer> countMap = commentLikeRepository.countByCommentIds(allIds);
        Set<Integer> likedSet = commentLikeRepository.findLikedCommentIdsByUser(viewerId, allIds);

        // DTO 변환 + likes 세팅
        return parents.stream().map(p -> {
            List<CommentResponse.ItemDTO> childDtos = childrenMap.getOrDefault(p.getId(), List.of())
                    .stream().map(c -> toItemWithLikes(c, viewerId, postAuthorId, countMap, likedSet)).toList();
            CommentResponse.ItemDTO parentDto = toItemWithLikes(p, viewerId, postAuthorId, countMap, likedSet);
            parentDto.setChildren(childDtos);
            return parentDto;
        }).toList();
    }

    private CommentResponse.ItemDTO toItemWithLikes(Comment c,
                                                    Integer viewerId,
                                                    Integer postAuthorId,
                                                    Map<Integer, Integer> countMap,
                                                    Set<Integer> likedSet) {
        var dto = new CommentResponse.ItemDTO(c);

        dto.setOwner(viewerId != null && viewerId.equals(c.getUser().getId()));

        dto.setPostAuthor(Objects.equals(c.getUser().getId(), postAuthorId));


        int likeCount = countMap.getOrDefault(c.getId(), 0);
        boolean liked = likedSet.contains(c.getId());
        dto.setLikes(new CommentLikeResponse.LikesDTO(likeCount, liked));
        return dto;
    }
}