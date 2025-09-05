package com.mtcoding.minigram.posts.comments.likes;

import lombok.AllArgsConstructor;
import lombok.Data;

public class CommentLikeResponse {

    @Data
    @AllArgsConstructor
    public static class LikesDTO {
        private Integer count;
        private Boolean isLiked;
    }
}
