package com.mtcoding.minigram.posts.likes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PostLikeResponse {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikesDTO {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer count;   // 스토리에서는 null → JSON에 안 내려감
        private Boolean isLiked;
    }
}
