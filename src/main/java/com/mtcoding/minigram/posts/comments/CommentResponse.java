package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram._core.constants.CommentConstants;
import com.mtcoding.minigram._core.constants.UserDetailConstants;
import com.mtcoding.minigram.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ListDTO {
        private Integer current;     // 현재 페이지(0-base)
        private Integer size;        // 페이지당 개수
        private Integer totalCount;  // 전체 글 수
        private Integer totalPage;   // 전체 페이지 수
        private Integer prev;        // current - 1
        private Integer next;        // current + 1
        private Boolean isFirst;     // current == 0
        private Boolean isLast;      // (totalPage - 1) == current
        private List<ItemDTO> commentList;

        public ListDTO(List<ItemDTO> commentList, Integer current, Integer totalCount) {
            this.commentList = commentList;
            this.current = current;
            this.size = CommentConstants.ITEMS_PER_PAGE;
            this.totalCount = totalCount;
            this.totalPage = makeTotalPage(totalCount, size); // 2
            this.prev = Math.max(0, current - 1);
            this.next = totalPage == 0 ? 0 : Math.min(totalPage - 1, current + 1);
            this.isFirst = current == 0;
            this.isLast = totalPage == 0 || current.equals(totalPage - 1);
        }

        private Integer makeTotalPage(int totalCount, int size) {
            int rest = totalCount % size > 0 ? 1 : 0;
            return totalCount / size + rest;
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

    @Data
    public static class DTO {
        private Integer commentId;
        private Integer postId;
        private Integer userId;
        private Integer rootId;
        private Integer parentId;
        private String content;
        private CommentStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updateAt;

        public DTO(Comment comment) {
            this.commentId = comment.getId();
            this.postId = comment.getPost().getId();
            this.userId = comment.getUser().getId();
            this.rootId = comment.getRoot()!= null ? comment.getRoot().getId() : null;;
            this.parentId = comment.getParent()!= null ? comment.getParent().getId() : null;;
            this.content = comment.getContent();
            this.status = comment.getStatus();
            this.createdAt = comment.getCreatedAt();
            this.updateAt = comment.getUpdatedAt();
        }
    }
}

