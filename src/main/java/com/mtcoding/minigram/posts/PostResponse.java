package com.mtcoding.minigram.posts;

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

        public DetailDTO(Post post,
                         List<PostImage> images,
                         int likeCount, boolean isLiked,
                         int commentCount,
                         boolean isOwner, boolean isFollowing,
                         boolean isReported) {

            this.postId = post.getId();
            this.content = post.getContent();
            this.postedAt = post.getCreatedAt();

            // author: 생성 시점에 owner/following 주입
            this.author = new AuthorDTO(
                    post.getUser().getId(),
                    post.getUser().getUsername(),
                    post.getUser().getProfileImageUrl(),
                    isFollowing,
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
        private List<ItemDTO> postList;

        public FeedDTO(List<ItemDTO> postList) {
            this.postList = postList;
        }
    }

    @Data
    public static class ItemDTO{
        private UserDTO user;
        private List<PostImageDTO> postImageList;
        private Integer postId;
        private String content;
        private Boolean isLiked;
        private Integer likesCount;
        private Integer commentCount;
        private LocalDateTime createdAt;


        @Data
        public class UserDTO{
            private Integer userId;
            private String username;
            private String profileImageUrl;

            public UserDTO(User user) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
            }
        }

        @Data
        public class PostImageDTO{
            private Integer postImageId;
            private String url;

            public PostImageDTO(PostImage postImage) {
                this.postImageId = postImage.getId();
                this.url = postImage.getUrl();
            }
        }

        public ItemDTO(Post post, Boolean isLiked, Integer likesCount, Integer commentCount, List<PostImage> postImageList) {
            this.user = new UserDTO(post.getUser());
            this.postImageList = postImageList.stream().map(postImage -> new PostImageDTO(postImage)).toList();
            this.postId = post.getId();
            this.content = post.getContent();
            this.isLiked = isLiked;
            this.likesCount = likesCount;
            this.commentCount = commentCount;
            this.createdAt = post.getCreatedAt();
        }
    }
}
