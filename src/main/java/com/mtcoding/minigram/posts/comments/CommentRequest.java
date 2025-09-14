package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.users.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 1000, message = "댓글은 최대 1000자까지 작성할 수 있습니다.")
        private String content;

        // 최상위 댓글이면 null, 대댓글이면 parentId 전달
        private Integer parentId;

        public Comment toEntity(Post post, Comment root, Comment parent, User user) {
            return Comment.builder()
                    .post(post)
                    .content(content)
                    .root(root)
                    .parent(parent)
                    .user(user)
                    .status(CommentStatus.ACTIVE)
                    .build();
        }
    }
}
