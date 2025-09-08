package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ListDTO {
        private final List<ItemDTO> items;
    }

    @Data
    public static class ItemDTO {
        private Integer commentId;
        private UserDTO user;
        private String content;
        private LocalDateTime createdAt;
        private Integer parentId;                 // null이면 최상위
        private List<ItemDTO> children = List.of();
        private LikesDTO likes;                   // ← 내부 클래스로 교체
        private Boolean isOwner;
        private Boolean isPostAuthor;

        @Data
        public static class UserDTO {
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
        @AllArgsConstructor
        public static class LikesDTO {
            private Integer count;
            private Boolean isLiked;

            public LikesDTO(int count, boolean liked) {
                this.count = count;
                this.isLiked = liked;
            }
        }

        public ItemDTO(Comment comment,
                       List<ItemDTO> children,
                       LikesDTO likes,
                       boolean isOwner,
                       boolean isPostAuthor) {
            this.commentId = comment.getId();
            this.user = new UserDTO(comment.getUser());
            this.content = comment.getContent();
            this.createdAt = comment.getCreatedAt();
            this.parentId = (comment.getParent() == null) ? null : comment.getParent().getId();
            this.children = (children == null) ? List.of() : children;
            this.likes = likes;
            this.isOwner = isOwner;
            this.isPostAuthor = isPostAuthor;
        }

        public static ItemDTO from(
                Comment comment,
                List<ItemDTO> children,
                int likeCount,
                boolean liked,
                boolean owner,
                boolean postAuthor
        ) {
            return new ItemDTO(
                    comment,
                    (children == null) ? List.of() : children,
                    new LikesDTO(likeCount, liked),
                    owner,
                    postAuthor
            );
        }
    }
}
