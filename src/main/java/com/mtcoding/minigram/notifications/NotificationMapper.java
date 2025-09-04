package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram.users.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class NotificationMapper {

    private NotificationMapper() {
    }

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static NotificationResponse.ItemDTO toItemDTO(Notification n) {
        return toItemDTO(n, null, null);
    }

    public static NotificationResponse.ItemDTO toItemDTO(Notification n, Long postIdForComment, String snippetFromDb) {
        // createdAt null-safe
        LocalDateTime created = n.getCreatedAt() != null
                ? n.getCreatedAt()
                : (n.getUpdatedAt() != null ? n.getUpdatedAt() : LocalDateTime.now());
        String createdStr = created.format(ISO);

        // 2) actor (sender)
        User s = n.getSender();
        Long actorId = s.getId().longValue();
        String username = safeUsername(s, actorId);
        String profile = placeholderProfile(actorId); // 실제 프로필 URL 붙일 때 여기 교체

        // 3) target (알림 타입별로 targetId 해석)
        Long postId = null;
        Long commentId = null;
        String postThumb = null;
        String commentSnippet = null;


        switch (n.getType()) {
            case POST_LIKED -> {
                postId = n.getTargetId().longValue();              // post.id
                postThumb = "https://picsum.photos/seed/p" + postId + "/120";
            }
            case COMMENTED -> {
                commentId = n.getTargetId().longValue();           // comment.id
                postId = postIdForComment;                         // ⬅ 조회해온 postId 주입
                if (postId != null) postThumb = "https://picsum.photos/seed/p" + postId + "/120";
                commentSnippet = truncate(snippetFromDb, 40);
            }
            case FOLLOWED -> {  /* target 없음 */ }
        }

        // 3) FOLLOW만 target=null, 나머지는 new TargetDTO(...)
        NotificationResponse.TargetDTO targetDto =
                (n.getType() == NotificationType.FOLLOWED)
                        ? null
                        : new NotificationResponse.TargetDTO(postId, commentId, postThumb, commentSnippet);

        // 4) 화면용 타입 + 메시지
        String clientType = toClientType(n.getType()); // "POST_LIKE" / "COMMENT" / "FOLLOW"
        String message = buildMessage(n.getType(), username, commentSnippet);

        // 5) read 여부
        boolean read = n.getStatus() == ReadStatus.READ;

        // 6) DTO 조립
        return new NotificationResponse.ItemDTO(
                n.getId().longValue(),
                clientType,
                createdStr,
                read,
                new NotificationResponse.ActorDTO(actorId, username, profile),
                targetDto,
                message
        );
    }

    // ===== helpers =====

    private static String toClientType(NotificationType t) {
        return switch (t) {
            case POST_LIKED -> "POST_LIKE";
            case COMMENTED -> "COMMENT";
            case FOLLOWED -> "FOLLOW";
        };
    }

    private static String buildMessage(NotificationType t, String username, String snippet) {
        String nick = (username == null || username.isBlank()) ? "사용자" : username;
        return switch (t) {
            case POST_LIKED -> nick + "님이 회원님의 게시글을 좋아합니다.";
            case COMMENTED -> nick + "님이 댓글을 남겼습니다: " + truncate(snippet, 40);
            case FOLLOWED -> nick + "님이 회원님을 팔로우하기 시작했습니다.";
        };
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        s = s.trim().replaceAll("\\s+", " ");
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }

    private static String placeholderProfile(Long actorId) {
        // User에 프로필 이미지 필드가 준비되면 여기서 교체해서 반환해줘.
        return "https://picsum.photos/seed/u" + actorId + "/80";
    }

    private static String safeUsername(User s, Long actorId) {
        String u = s.getUsername(); // User 엔티티 필드명에 맞게
        return (u == null || u.isBlank()) ? ("user" + actorId) : u;
    }
}
