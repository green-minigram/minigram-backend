package com.mtcoding.minigram.posts.likes;

import lombok.AllArgsConstructor;
import lombok.Data;

public class PostLikeResponse {

    @Data
    @AllArgsConstructor
    public static class LikesDTO {
        private Integer count;
        private Boolean isLiked;
    }
}
