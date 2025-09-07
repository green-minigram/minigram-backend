package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.PostRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeRepository;
import com.mtcoding.minigram.posts.comments.likes.CommentLikeResponse;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
    private final UserRepository userRepository;


    //게시글 댓글 조회
    public List<CommentResponse.ItemDTO> findAllByPostId(Integer postId, Integer userId) {

        Integer postAuthorId = postRepository.findAuthorIdByPostId(postId);
        if (postAuthorId == null) throw new ExceptionApi404("게시글이 존재하지 않습니다"); // [CHANGED] 존재하지 않는 게시글 처리


        // 부모/자식 조회
        List<Comment> parents = commentRepository.findParentsByPostId(postId);
        if (parents.isEmpty()) return List.of();

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
        List<CommentResponse.ItemDTO> result = parents.stream()
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
                result.size(), children.size(), allIds.size(), likedSet.size()); // 테스트 확인 로그 추가

        return result;
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
    public CommentResponse.ItemDTO create(Integer postId, Integer userId, CommentRequest.CreateDTO req) {
        if (!StringUtils.hasText(req.getContent())) {
            throw new ExceptionApi400("댓글 내용을 입력해주세요.");
        }

        Integer postAuthorId = postRepository.findAuthorIdByPostId(postId);
        if (postAuthorId == null) throw new ExceptionApi404("게시글이 존재하지 않습니다.");

        User author = userRepository.findUserById(userId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));
        Post post = new Post(); // 엔티티 로딩을 최소화하고 싶으면 프록시 참조로 대체
        try {
            var f = Post.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(post, postId);
        } catch (Exception ignored) {
        }

        Comment parent = null;
        if (req.getParentId() != null) {
            parent = commentRepository.findCommentById(req.getParentId())
                    .orElseThrow(() -> new ExceptionApi404("부모 댓글을 찾을 수 없습니다."));
            if (!Objects.equals(parent.getPost().getId(), postId)) {
                throw new ExceptionApi400("부모 댓글과 게시글이 일치하지 않습니다.");
            }
        }

        Comment comment = Comment.builder()
                .post(parent == null ? postRepository.findPostById(postId).orElseThrow(() -> new ExceptionApi404("게시글이 존재하지 않습니다.")) : parent.getPost())
                .user(author)
                .parent(parent)
                .content(req.getContent())
                .status(CommentStatus.ACTIVE)   // ★ 추가
                .build();

        log.debug("NEW COMMENT status={}", comment.getStatus()); // 반드시 ACTIVE 찍혀야 함
        commentRepository.save(comment);

        // 작성 직후 기본값
        var likes = new CommentLikeResponse.LikesDTO(0, false);
        boolean owner = true;
        boolean isPostAuthor = Objects.equals(author.getId(), postAuthorId);

        return CommentResponse.ItemDTO.from(
                comment,
                List.of(), // children: 작성 직후 빈 리스트
                likes.getCount(),
                Boolean.TRUE.equals(likes.getIsLiked()), // ← 여기
                owner,
                isPostAuthor
        );
    }
}