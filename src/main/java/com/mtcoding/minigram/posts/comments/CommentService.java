package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public List<CommentResponse.ItemDTO> findAllByPostId(Integer postId, Integer userId) {

        Integer postAuthorId = postRepository.findAuthorIdByPostId(postId);

        // 부모/자식 조회
        List<Comment> parents = commentRepository.findParentsByPostId(postId);
        if (parents.isEmpty()) return List.of();

        List<Integer> parentIds = parents.stream().map(Comment::getId).toList();
        List<Comment> children = commentRepository.findChildrenByParentIds(parentIds);

        // parentId -> children 매핑
        Map<Integer, List<Comment>> childrenMap = new LinkedHashMap<>();
        parentIds.forEach(id -> childrenMap.put(id, new ArrayList<>()));
        for (Comment ch : children) {
            childrenMap.get(ch.getParent().getId()).add(ch);
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
        return parents.stream().map(p -> {
            List<CommentResponse.ItemDTO> childDtos = childrenMap.getOrDefault(p.getId(), List.of())
                    .stream()
                    .map(c -> toItemWithLikes(c, userId, postAuthorId, countMap, likedSet))
                    .toList();

            CommentResponse.ItemDTO parentDto = toItemWithLikes(p, userId, postAuthorId, countMap, likedSet);
            parentDto.setChildren(childDtos);
            return parentDto;
        }).toList();
    }

    private CommentResponse.ItemDTO toItemWithLikes(
            Comment c,
            Integer viewerId,
            Integer postAuthorId,
            Map<Integer, Integer> countMap,
            Set<Integer> likedSet
    ) {
        CommentResponse.ItemDTO itemDTO = new CommentResponse.ItemDTO(c);

        // 소유자/게시글 작성자 여부
        itemDTO.setOwner(viewerId != null && viewerId.equals(c.getUser().getId()));
        itemDTO.setPostAuthor(Objects.equals(c.getUser().getId(), postAuthorId));

        // 좋아요 정보
        int likeCount = countMap.getOrDefault(c.getId(), 0);
        boolean liked = likedSet.contains(c.getId());
        itemDTO.setLikes(new CommentLikeResponse.LikesDTO(likeCount, liked));

        return itemDTO;
    }
}