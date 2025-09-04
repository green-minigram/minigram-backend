package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.likes.PostLikeResponse.LikesDTO;
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

        public DetailDTO(Post post, List<PostImage> images) {
            this.postId = post.getId();
            this.author = new AuthorDTO(
                    post.getUser().getId(),
                    post.getUser().getUsername(),
                    post.getUser().getProfileImageUrl(),
                    false, // isFollowing (default)
                    false // isOwner (default)
            );
            this.images = images.stream()
                    .map(i -> new ImageDTO(i.getId(), i.getUrl()))
                    .collect(Collectors.toList());
            this.content = post.getContent();
            this.likes = new LikesDTO(0, false);
            this.commentCount = 0;
            this.postedAt = post.getCreatedAt();
            this.isReported = false;
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
