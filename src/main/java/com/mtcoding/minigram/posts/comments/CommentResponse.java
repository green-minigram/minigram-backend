package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.comments.likes.CommentLikeResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ItemDTO {
        private Integer commentId;
        private AuthorDTO author;
        private String content;
        private LocalDateTime createdAt;
        private Integer parentId;                 // null이면 최상위
        private List<ItemDTO> children = List.of(); // 대댓글 목록

        private CommentLikeResponse.LikesDTO likes;

        public ItemDTO(Comment c) {
            this.commentId = c.getId();
            this.author = new AuthorDTO(
                    c.getUser().getId(),
                    c.getUser().getUsername(),
                    c.getUser().getProfileImageUrl()
            );
            this.content = c.getContent();
            this.createdAt = c.getCreatedAt();
            this.parentId = (c.getParent() == null) ? null : c.getParent().getId();
        }

        public ItemDTO(Comment c, List<ItemDTO> children) {
            this(c);
            this.children = children;
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
}
