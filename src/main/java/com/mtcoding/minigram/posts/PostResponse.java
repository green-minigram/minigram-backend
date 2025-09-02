package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.posts.likes.PostLikeResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {

    @Data
    public static class DetailDTO {
        private Integer postId;
        private AuthorDTO author;
        private String content;
        private List<ImageDTO> images;
        private LocalDateTime createdAt;

        private PostLikeResponse.LikesDTO likes;
        private Integer commentCount;

        private Boolean isFollowing;   // viewer → author
        private Boolean isOwner;

        public DetailDTO(Post post, List<PostImage> images) {
            this.postId = post.getId();
            this.author = new AuthorDTO(
                    post.getUser().getId(),
                    post.getUser().getUsername(),
                    post.getUser().getProfileImageUrl()
            );
            this.content = post.getContent();
            this.images = images.stream()
                    .map(i -> new ImageDTO(i.getId(), i.getUrl()))
                    .collect(Collectors.toList());
            this.createdAt = post.getCreatedAt();

            this.likes = new PostLikeResponse.LikesDTO(0, false);
            this.commentCount = 0;
            this.isFollowing = false;
            this.isOwner = false;
        }
    }

    @Data
    public static class AuthorDTO {
        private Integer userId;
        private String username;
        private String profileImageUrl;

        public AuthorDTO(Integer userId, String username, String profileImageUrl) {
            this.userId = userId;
            this.username = username;
            this.profileImageUrl = profileImageUrl;
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
