package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.constants.FeedConstants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {

    @Data
    public static class DetailDTO {
        private Integer postId;
        private AuthorDTO author;
        private List<ImageDTO> images;
        private String content;
        private LikesDTO likes;
        private Integer commentCount;
        private LocalDateTime postedAt;
        private Boolean isReported;
        private Boolean isAd;

        public DetailDTO(Post post,
                         List<PostImage> images,
                         int likeCount, boolean isLiked,
                         int commentCount,
                         boolean isOwner, boolean isFollowing,
                         boolean isReported, boolean isAd) {

            this.postId = post.getId();
            this.content = post.getContent();
            this.postedAt = post.getCreatedAt();

            // author: 생성 시점에 owner/following 주입
            this.author = new AuthorDTO(
                    post.getUser().getId(),
                    post.getUser().getUsername(),
                    post.getUser().getProfileImageUrl(),
                    isAd ? null : isFollowing,
                    isOwner
            );

            // images 매핑
            this.images = images.stream()
                    .map(i -> new ImageDTO(i.getId(), i.getUrl()))
                    .collect(Collectors.toList());

            // likes / comments / report
            this.likes = new LikesDTO(likeCount, isLiked);
            this.commentCount = commentCount;
            this.isReported = isReported;
            this.isAd = isAd;
        }
    }

    @Data
    public static class SavedDTO {
        private Integer postId;
        private Integer userId;
        private String content;
        private PostStatus status;
        private LocalDateTime postedAt;
        private LocalDateTime updatedAt;
        private List<ImageDTO> images;

        public static SavedDTO from(Post post, List<PostImage> images) {
            SavedDTO dto = new SavedDTO();
            dto.postId = post.getId();
            dto.userId = post.getUser().getId();
            dto.content = post.getContent();
            dto.status = post.getStatus();
            dto.postedAt = post.getCreatedAt();
            dto.updatedAt = post.getUpdatedAt();
            dto.images = images.stream()
                    .map(pi -> new ImageDTO(pi.getId(), pi.getUrl()))
                    .toList();
            return dto;
        }
    }

    @Data
    @AllArgsConstructor
    public static class LikesDTO {
        private Integer count;
        private Boolean isLiked;

        public LikesDTO(int count, boolean liked) {
            this.count = count;
            this.isLiked = liked;
        }
    }

    @Data
    public static class AuthorDTO {
        private Integer userId;
        private String username;
        private String profileImageUrl;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean isFollowing;

        private Boolean isOwner;

        public AuthorDTO(Integer userId, String username, String profileImageUrl,
                         Boolean isFollowing, Boolean isOwner) {
            this.userId = userId;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
            this.isFollowing = isFollowing;
            this.isOwner = isOwner;
        }
    }

    @Data
    public static class ImageDTO {
        private Integer id;
        private String url;

        public ImageDTO(Integer id, String url) {
            this.id = id;
            this.url = url;
        }
    }

    @Data
    @AllArgsConstructor
    public static class DeleteDTO {
        private Integer postId;
        private Boolean deleted; // 항상 true로 반환 (멱등)
    }


    @Data
    public static class FeedDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<ItemDTO> postList;

        public FeedDTO(List<ItemDTO> postList, Integer current, Integer postTotalCount) {
            this.postList = postList;
            this.current = current;
            this.size = postList.size();
            this.totalCount = postTotalCount;
            this.totalPage = makeTotalPage(totalCount);
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = totalPage == 0 || current.equals(totalPage - 1);
        }

        private int makeTotalPage(int totalCount) {
            int postsPerPage = FeedConstants.POSTS_PER_PAGE;
            return (totalCount + postsPerPage - 1) / postsPerPage;
        }
    }

    @Data
    public static class ItemDTO {
        private Boolean isAdvertisement;
        private Integer postId;
        private String content;
        private Boolean isOwner;
        private Boolean isLiked;
        private Integer likesCount;
        private Integer commentCount;
        private LocalDateTime createdAt;
        private UserDTO user;
        private List<PostImageDTO> postImageList;


        @Data
        public class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
            private Boolean isFollowing;

            public UserDTO(User user, Boolean isFollowing) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
                this.isFollowing = isFollowing;
            }
        }

        @Data
        public class PostImageDTO {
            private Integer postImageId;
            private String url;

            public PostImageDTO(PostImage postImage) {
                this.postImageId = postImage.getId();
                this.url = postImage.getUrl();
            }
        }

        public ItemDTO(Post post, Boolean isAdvertisement, Boolean isFollowing, Boolean isOwner, Boolean isLiked, Integer likesCount, Integer commentCount, List<PostImage> postImageList) {
            this.user = new UserDTO(post.getUser(), isFollowing);
            this.postImageList = postImageList.stream().map(postImage -> new PostImageDTO(postImage)).toList();
            this.isAdvertisement = isAdvertisement;
            this.postId = post.getId();
            this.content = post.getContent();
            this.isOwner = isOwner;
            this.isLiked = isLiked;
            this.likesCount = likesCount;
            this.commentCount = commentCount;
            this.createdAt = post.getCreatedAt();
        }
    }

    @Data
    public static class SearchDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<SearchItemDTO> postList;

        public SearchDTO(List<SearchItemDTO> postList, Integer current, Integer totalCount) {
            this.postList = postList;
            this.current = current;
            this.size = 12;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size);
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = (totalPage - 1) == current;
        }

        private int makeTotalPage(int totalCount, int size) {
            if (size <= 0) return 0;
            int rest = (totalCount % size) > 0 ? 1 : 0;
            return (totalCount / size) + rest;
        }
    }

    @Data
    public static class SearchItemDTO {
        private Integer postId;
        private String postImageUrl;
        private String content;

        public SearchItemDTO(Integer postId, String postImageUrl, String content) {
            this.postId = postId;
            this.postImageUrl = postImageUrl;
            this.content = content;
        }
    }
}
