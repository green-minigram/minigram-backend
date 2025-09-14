package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ListDTO {
        private List<ItemDTO> commentList;

        public ListDTO(List<ItemDTO> commentList) {
            this.commentList = commentList;
        }
    }

    @Data
    public static class ItemDTO {
        private Integer commentId;
        private Integer rootId;
        private Integer parentId;
        private String content;
        private Boolean isOwner;
        private Boolean isLiked;
        private Integer likeCount;
        private LocalDateTime createdAt;
        private UserDTO user;

        @Data
        public class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
            private Boolean hasUnseen;

            public UserDTO(User user, Boolean hasUnseen) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
                this.hasUnseen = hasUnseen;
            }
        }

        public ItemDTO(Comment comment, Boolean isOwner, Boolean isLiked, Integer likeCount, Boolean hasUnseen) {
            this.commentId = comment.getId();
            this.rootId = (comment.getRoot() != null) ? comment.getRoot().getId() : null;
            this.parentId = (comment.getParent() != null) ? comment.getParent().getId() : null;
            this.content = comment.getContent();
            this.isOwner = isOwner;
            this.isLiked = isLiked;
            this.likeCount = likeCount;
            this.createdAt = comment.getCreatedAt();
            this.user = new UserDTO(comment.getUser(), hasUnseen);
        }
    }

    @Data
    @AllArgsConstructor
    public static class DeleteDTO {
        private Integer commentId;
        private String message;
    }
}

