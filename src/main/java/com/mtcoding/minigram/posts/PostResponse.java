package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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

        public DetailDTO(Post post,
                         java.util.List<PostImage> images,
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
                    .collect(java.util.stream.Collectors.toList());

            // likes / comments / report
            this.likes = new LikesDTO(likeCount, isLiked);
            this.commentCount = commentCount;
            this.isReported = isReported;
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
}
