package com.mtcoding.minigram.posts.comments;

import lombok.Data;
import lombok.NoArgsConstructor;

public class CommentRequest {

    @Data
    @NoArgsConstructor
    public static class CreateDTO {
        private String content;     // 댓글 본문
        private Integer parentId;   // 대댓글이면 부모 댓글 id, 아니면 null
    }
}
