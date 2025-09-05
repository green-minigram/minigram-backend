package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.comments.likes.CommentLikeResponse;
import com.mtcoding.minigram.users.UserResponse.SummaryDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Data
    public static class ItemDTO {
        private Integer commentId;
        private SummaryDTO user;
        private String content;
        private LocalDateTime createdAt;
        private Integer parentId;                 // null이면 최상위
        private List<ItemDTO> children = List.of(); // 대댓글 목록
        private CommentLikeResponse.LikesDTO likes;
        private boolean isOwner;
        private boolean isPostAuthor;


        public ItemDTO(Comment comment,
                       List<ItemDTO> children,
                       CommentLikeResponse.LikesDTO likes,
                       boolean isOwner,
                       boolean isPostAuthor) {
            this.commentId = comment.getId();
            this.user = new SummaryDTO(comment.getUser());
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
                    new CommentLikeResponse.LikesDTO(likeCount, liked),
                    owner,
                    postAuthor
            );
        }
    }
}
