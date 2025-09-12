package com.mtcoding.minigram.feed;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.mtcoding.minigram.advertisements.Advertisement;
import com.mtcoding.minigram.advertisements.AdvertisementRepository;
import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.images.PostImageRepository;
import com.mtcoding.minigram.stories.Story;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FeedService {
    private final FeedRepository feedRepository;
    private final PostImageRepository postImageRepository;
    private final AdvertisementRepository advertisementRepository;

    public FeedResponse.PostListDTO findPosts(Integer page, Integer currentUserId) {
        // 1. 일반 게시글
        // 1-1. 게시글 총 개수 조회
        int postTotalCount = Math.toIntExact(feedRepository.getPostCountFromFolloweesIncludingMine(currentUserId));

        // 1-2. Post, likesCount, isLiked, commentCount 조회
        List<Object[]> postObsList = feedRepository.findPostsFromFolloweesIncludingMine(page, currentUserId);
        if (postObsList.isEmpty()) return new FeedResponse.PostListDTO(List.of(), page, postTotalCount);

        // 1-3. 게시글 리스트 + 게시글 Id 리스트 조립
        record PostRow(Post post, int likesCount, boolean isLiked, int commentCount, boolean isFollowing,
                       boolean isOwner) {
        }
        List<PostRow> postRowList = new ArrayList<>(postObsList.size());
        List<Integer> postIdList = new ArrayList<>(postObsList.size());

        for (Object[] obs : postObsList) {
            Post post = (Post) obs[0];
            int likesCount = Math.toIntExact((Long) obs[1]);
            boolean isLiked = (Boolean) obs[2];
            int commentCount = Math.toIntExact((Long) obs[3]);
            boolean isFollowing = (Boolean) obs[4];
            boolean isOwner = post.getUser().getId().equals(currentUserId);

            postRowList.add(new PostRow(post, likesCount, isLiked, commentCount, isFollowing, isOwner));

            postIdList.add(post.getId());
        }

        // 2. 광고
        // 2-1. 광고 총 개수 조회
        LocalDateTime now = LocalDateTime.now();
        int adTotalCount = Math.toIntExact(advertisementRepository.totalCount(now));

        // 2-2. Advertisement, likesCount, isLiked, commentCount 조회
        List<Object[]> adObsList = advertisementRepository.findAllValid(page, currentUserId, now, adTotalCount);

        // 2-3. 광고 리스트 + 광고 Id 리스트 조립
        record AdRow(Advertisement ad, int likesCount, boolean isLiked, int commentCount, boolean isFollowing,
                     boolean isOwner) {
        }
        List<AdRow> adRowList = new ArrayList<>(adObsList.size());
        List<Integer> adPostIdList = new ArrayList<>(adObsList.size());

        for (Object[] obs : adObsList) {
            Advertisement ad = (Advertisement) obs[0];
            int likesCount = Math.toIntExact((Long) obs[1]);
            boolean isLiked = (Boolean) obs[2];
            int commentCount = Math.toIntExact((Long) obs[3]);

            adRowList.add(new AdRow(ad, likesCount, isLiked, commentCount, false, false));
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
        List<FeedResponse.PostItemDTO> postItemDTOList = new ArrayList<>(FeedConstants.ITEMS_PER_PAGE);

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
                    postItemDTOList.add(new FeedResponse.PostItemDTO(
                            adRow.ad().getPost(),
                            true,
                            adRow.isFollowing(), // TODO : 관리자 팔로우 불가 전제로 고정 -> 체크 필요
                            adRow.isOwner(),
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
                    postItemDTOList.add(new FeedResponse.PostItemDTO(
                            postRow.post(),
                            false,
                            postRow.isFollowing(),
                            postRow.isOwner(),
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

        return new FeedResponse.PostListDTO(postItemDTOList, page, postTotalCount);
    }

    // 피드 스토리 프리뷰 목록 조회
    public FeedResponse.PreviewListDTO findStoryPreviews(Integer page, Integer currentUserId) {
        // 1. userId, username, profileImageUrl, hasUnseen 조회
        List<Object[]> obsList = feedRepository.findStoriesFromFollowees(page, currentUserId);

        // 2. ItemDTO 조립
        List<FeedResponse.PreviewItemDTO> itemDTOList = obsList.stream()
                .map(obs -> new FeedResponse.PreviewItemDTO(
                        (Integer) obs[0],
                        (String) obs[1],
                        (String) obs[2],
                        (Boolean) obs[3]
                ))
                .toList();

        // 3. totalCount 조회
        Integer totalCount = Math.toIntExact(feedRepository.getStoriesCountFromFollowees(currentUserId));

        return new FeedResponse.PreviewListDTO(itemDTOList, page, totalCount);
    }

    public FeedResponse.StoryListDTO findMyStories(Integer currentUserId) {
        List<Object[]> obsList = feedRepository.findMyStories(currentUserId);

        List<FeedResponse.StoryItemDTO> itemDTOList = obsList.stream().map(ob -> {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            Integer likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            boolean isOwner = true;

            return new FeedResponse.StoryItemDTO(story, isFollowing, isOwner, isLiked, likeCount);
        }).toList();

        return new FeedResponse.StoryListDTO(itemDTOList);
    }

    public FeedResponse.StoryListDTO findStoriesByUserId(Integer userId, Integer currentUserId) {
        List<Object[]> obsList = feedRepository.findStoriesByUserId(userId, currentUserId);

        List<FeedResponse.StoryItemDTO> itemDTOList = obsList.stream().map(ob -> {
            Story story = (Story) ob[0];
            Boolean isFollowing = (Boolean) ob[1];
            Integer likeCount = ((Long) ob[2]).intValue();
            Boolean isLiked = (Boolean) ob[3];

            Boolean isOwner = story.getUser().getId().equals(currentUserId);

            return new FeedResponse.StoryItemDTO(story, isFollowing, isOwner, isLiked, likeCount);
        }).toList();

        return new FeedResponse.StoryListDTO(itemDTOList);
    }
}
