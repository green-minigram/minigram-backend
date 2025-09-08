package com.mtcoding.minigram.notifications;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class NotificationResponse {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ListDTO {
        private List<NotificationResponse.ItemDTO> items; // 타입을 완전히 명시
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(NON_NULL) //알람 타입이 다를때 빈배열[] 이거 대신 아무것도 안보내는 코드
    public static class ItemDTO {
        private Long id;                 // 알림 id
        private String type;             // POST_LIKE / FOLLOW / COMMENT
        private String createdAt;
        private boolean read;            // ISO 문자열

        private ActorDTO user;          // 행위자

        private TargetDTO post;        // 타겟(게시글/댓글)
        private String message;     // ⬅ 화면표시용 문구 (서버에서 생성)

    }

    @Data
    @AllArgsConstructor
    public static class ActorDTO {
        private Long userId;
        private String username;
        private String profileImageUrl;
    }

    @Data
    @AllArgsConstructor
    @JsonInclude(NON_NULL)
    public static class TargetDTO {
        private Long postId;
        private Long commentId;
        private String postImage;
        private String comment;
    }
}
