package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.follows.FollowRepository;
import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.images.PostImageRepository;
import com.mtcoding.minigram.posts.likes.PostLikeRepository;
import com.mtcoding.minigram.reports.ReportRepository;
import com.mtcoding.minigram.users.User;
import com.mtcoding.minigram.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

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
    public PostResponse.SavedDTO create(PostRequest.CreateDTO req, Integer authorId) {

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ExceptionApi404("사용자를 찾을 수 없습니다."));

        // 이미지 URL 목록을 null-세이프하게 정제(trim)한 뒤 빈 값 제거·중복 제거한 리스트 생성
        List<String> cleanedUrls = Optional.ofNullable(req.getImageUrls())
                .orElse(List.of())
                .stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();


        if (cleanedUrls.isEmpty()) {
            throw new ExceptionApi400("이미지 최소 1장은 필요합니다.");
        }
        if (cleanedUrls.size() > 10) {
            throw new ExceptionApi400("이미지는 최대 10장까지 가능합니다.");
        }


        // 1) Post 저장 (status NOT NULL 주의)
        Post post = Post.builder()
                .user(author)
                .content(req.getContent())
                .status(PostStatus.ACTIVE)
                .build();
        postRepository.save(post);

        // 2) PostImage 저장 (정렬 부여 + 필요시 post.images 동기화)
        List<PostImage> savedImages = new ArrayList<>(req.getImageUrls().size());

        for (String url : req.getImageUrls()) {
            PostImage pi = PostImage.builder()
                    .post(post)
                    .url(url)
                    .build();
            postImageRepository.save(pi);
            savedImages.add(pi);
        }

        // 3) 상세 DTO 반환
        return PostResponse.SavedDTO.from(post, savedImages);
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

        post.markDeleted(); // 소프트 삭제
        return new PostResponse.DeleteDTO(post.getId(), true);
    }

    public PostResponse.FeedDTO getFeedPosts(Integer page, Integer currentUserId) {
        // 1. PostRow, postIdList 조립
        // 1-1. Post, likesCount, isLiked, commentCount 조회
        List<Object[]> obsList = postRepository.findFromFollowees(page, currentUserId);

        // 1-2. totalCount 조회
        Integer totalCount = Math.toIntExact(postRepository.totalCountFromFollowees(currentUserId));

        if (obsList.isEmpty()) return new PostResponse.FeedDTO(List.of(), page, totalCount);

        record PostRow(Post post, Boolean isLiked, Integer likesCount, Integer commentCount) {
        }

        List<PostRow> rows = new ArrayList<>(obsList.size());
        List<Integer> postIdList = new ArrayList<>(obsList.size());

        for (Object[] obs : obsList) {
            Post post = (Post) obs[0];
            int likesCount = Math.toIntExact((Long) obs[1]);
            Boolean isLiked = (Boolean) obs[2];
            int commentCount = Math.toIntExact((Long) obs[3]);

            rows.add(new PostRow(post, isLiked, likesCount, commentCount));

            postIdList.add(post.getId());
        }

        // 2. postId 기반 postImage 조회
        List<PostImage> postImageList = postImageRepository.findAllByPostIdIn(postIdList);

        // 3. postImageList를 postId 기준으로 그룹핑
        Map<Integer, List<PostImage>> postImageMap = postImageList.stream().collect(Collectors.groupingBy(postImage -> postImage.getPost().getId()));

        // 4. PostRow, PostImageMap -> ItemDTO 조립
        List<PostResponse.ItemDTO> itemDTOList = rows.stream()
                .map(row -> new PostResponse.ItemDTO(
                        row.post(),
                        false,
                        true,
                        row.isLiked(),
                        row.likesCount(),
                        row.commentCount(),
                        postImageMap.getOrDefault(row.post().getId(), List.of())
                ))
                .toList();

        return new PostResponse.FeedDTO(itemDTOList, page, totalCount);
    }

    public PostResponse.SearchDTO search(Integer page, String keyword) {
        // 1. 게시글 조회
        List<PostResponse.SearchItemDTO> searchItemDTOList = postRepository.findAllByKeyword(page, keyword);

        // 2. totalCount 조회
        Integer totalCount = Math.toIntExact(postRepository.totalCountByKeyword(keyword));

        return new PostResponse.SearchDTO(searchItemDTOList, page, totalCount);
    }
}

