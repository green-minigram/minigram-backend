package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.mtcoding.minigram._core.error.ex.ExceptionApi400;
import com.mtcoding.minigram._core.error.ex.ExceptionApi403;
import com.mtcoding.minigram._core.error.ex.ExceptionApi404;
import com.mtcoding.minigram.advertisements.Advertisement;
import com.mtcoding.minigram.advertisements.AdvertisementRepository;
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

import java.time.LocalDateTime;
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
    private final AdvertisementRepository advertisementRepository;

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
        // 1. 일반 게시글
        // 1-1. 게시글 총 개수 조회
        int postTotalCount = Math.toIntExact(postRepository.totalCountFromFollowees(currentUserId));

        // 1-2. Post, likesCount, isLiked, commentCount 조회
        List<Object[]> postObsList = postRepository.findFromFollowees(page, currentUserId);
        if (postObsList.isEmpty()) return new PostResponse.FeedDTO(List.of(), page, postTotalCount);

        // 1-3. 게시글 리스트 + 게시글 Id 리스트 조립
        record PostRow(Post post, int likesCount, boolean isLiked, int commentCount) {
        }
        List<PostRow> postRowList = new ArrayList<>(postObsList.size());
        List<Integer> postIdList = new ArrayList<>(postObsList.size());

        for (Object[] obs : postObsList) {
            Post post = (Post) obs[0];
            int likesCount = Math.toIntExact((Long) obs[1]);
            boolean isLiked = (Boolean) obs[2];
            int commentCount = Math.toIntExact((Long) obs[3]);

            postRowList.add(new PostRow(post, likesCount, isLiked, commentCount));

            postIdList.add(post.getId());
        }

        // 2. 광고
        // 2-1. 광고 총 개수 조회
        LocalDateTime now = LocalDateTime.now();
        int adTotalCount = Math.toIntExact(advertisementRepository.totalCount(now));

        // 2-2. Advertisement, likesCount, isLiked, commentCount 조회
        List<Object[]> adObsList = advertisementRepository.findAllValid(page, currentUserId, now, adTotalCount);

        // 2-3. 광고 리스트 + 광고 Id 리스트 조립
        record AdRow(Advertisement ad, int likesCount, boolean isLiked, int commentCount) {
        }
        List<AdRow> adRowList = new ArrayList<>(adObsList.size());
        List<Integer> adPostIdList = new ArrayList<>(adObsList.size());

        for (Object[] obs : adObsList) {
            Advertisement ad = (Advertisement) obs[0];
            int likesCount = Math.toIntExact((Long) obs[1]);
            boolean isLiked = (Boolean) obs[2];
            int commentCount = Math.toIntExact((Long) obs[3]);

            adRowList.add(new AdRow(ad, likesCount, isLiked, commentCount));
            adPostIdList.add(ad.getPost().getId());
        }

        // 3. 이미지
        // 3-1. 일반 게시글 + 광고 postIdList 합치기
        List<Integer> allPostIdList = new ArrayList<>(postIdList.size() + adPostIdList.size());
        allPostIdList.addAll(postIdList);
        allPostIdList.addAll(adPostIdList);

        // 3-2. postId 기반 postImage 조회 및 postId 기준으로 그룹핑
        Map<Integer, List<PostImage>> postImageMap = postImageRepository.findAllByPostIdIn(allPostIdList)
                .stream()
                .collect(Collectors.groupingBy(postImage -> postImage.getPost().getId()));

        // 3-3. 4+1+4+1 패턴으로 섞어서 itemDTO 리스트 만들기
        List<PostResponse.ItemDTO> itemDTOList = new ArrayList<>(FeedConstants.ITEMS_PER_PAGE);

        int postCount = 0, adCount = 0;

        for (int slot = 0; slot < FeedConstants.ITEMS_PER_PAGE; slot++) {
            boolean isAdSlot = (slot == FeedConstants.AD_SLOTS[0] || slot == FeedConstants.AD_SLOTS[1]);

            // (1) 광고 자리이면
            if (isAdSlot) {
                if (!adRowList.isEmpty()) {
                    AdRow adRow = adRowList.get(adCount % adRowList.size());
                    adCount++;

                    // postImageMap에서 광고 게시글의 이미지 추출
                    int adPostId = adRow.ad().getPost().getId();
                    List<PostImage> postImageList = postImageMap.getOrDefault(adPostId, List.of());

                    // ItemDTO 조립 후 list에 추가
                    itemDTOList.add(new PostResponse.ItemDTO(
                            adRow.ad().getPost(),
                            true,
                            false, // TODO : 관리자 팔로우 불가 전제로 고정 -> 체크 필요
                            adRow.isLiked(),
                            adRow.likesCount(),
                            adRow.commentCount(),
                            postImageList
                    ));
                }
            }
            // (2) 일반 게시글 자리이면
            else {
                if (postCount < postRowList.size()) {
                    PostRow postRow = postRowList.get(postCount++);

                    // // postImageMap에서 일반 게시글의 이미지 추출
                    List<PostImage> postImageList = postImageMap.getOrDefault(postRow.post().getId(), List.of());

                    // ItemDTO 조립 후 list에 추가
                    itemDTOList.add(new PostResponse.ItemDTO(
                            postRow.post(),
                            false,
                            true, // TODO : 내 게시글 조회 후 수정 필요
                            postRow.isLiked(),
                            postRow.likesCount(),
                            postRow.commentCount(),
                            postImageList
                    ));
                } else {
                    break;
                }
            }
        }

        return new PostResponse.FeedDTO(itemDTOList, page, postTotalCount);
    }

    public PostResponse.SearchDTO search(Integer page, String keyword) {
        // 1. 게시글 조회
        List<PostResponse.SearchItemDTO> searchItemDTOList = postRepository.findAllByKeyword(page, keyword);

        // 2. totalCount 조회
        Integer totalCount = Math.toIntExact(postRepository.totalCountByKeyword(keyword));

        return new PostResponse.SearchDTO(searchItemDTOList, page, totalCount);
    }
}

