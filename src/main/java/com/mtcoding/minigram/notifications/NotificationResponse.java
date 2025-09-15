package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram.users.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {
    @Data
    public static class ListDTO {
        private List<ItemDTO> notificationList;

        public ListDTO(List<ItemDTO> notificationList) {
            this.notificationList = notificationList;
        }
    }

    @Data
    public static class ItemDTO {
        private Integer notificationId;
        private NotificationType type; // POST_LIKED, COMMENTED, FOLLOWED
        private UserDTO sender;
        private Integer targetId; // post_like.id / comment.id / follow.id / story_like.id
        private Integer postId; // 댓글/게시글 좋아요일때만
        private String postImageUrl;
        private String commentContent; // 댓글일 때만
        private Integer storyId; // 스토리 좋아요일때만
        private String storyThumbnailUrl; // 스토리 좋아요일때만
        private LocalDateTime createdAt;
        private ReadStatus readStatus;

        @Data
        public class UserDTO {
            private Integer userId;
            private String username;
            private String profileImageUrl;
            private Boolean isFollowing;

            public UserDTO(User user, Boolean isFollowing) {
                this.userId = user.getId();
                this.username = user.getUsername();
                this.profileImageUrl = user.getProfileImageUrl();
                this.isFollowing = isFollowing;
            }
        }

        public ItemDTO(Notification notification, Boolean isFollowing, Integer postId, String postImageUrl, String commentContent, Integer storyId, String storyThumbnailUrl) {
            this.notificationId = notification.getId();
            this.type = notification.getType();
            this.sender = new UserDTO(notification.getSender(), isFollowing);
            this.targetId = notification.getTargetId();
            this.postId = postId;
            this.postImageUrl = postImageUrl;
            this.commentContent = commentContent;
            this.storyId = storyId;
            this.storyThumbnailUrl = storyThumbnailUrl;
            this.createdAt = notification.getCreatedAt();
            this.readStatus = notification.getStatus();
        }
    }

    @Data
    public static class DTO {
        private Integer notificationId;
        private NotificationType type;
        private Integer senderId;
        private Integer recipientId;
        private Integer targetId;
        private ReadStatus readStatus;
        private LocalDateTime reatedAt;
        private LocalDateTime createdAt;

        public DTO(Notification notification) {
            this.notificationId = notification.getId();
            this.type = notification.getType();
            this.senderId = notification.getSender().getId();
            this.recipientId = notification.getRecipient().getId();
            this.targetId = notification.getTargetId();
            this.readStatus = notification.getStatus();
            this.reatedAt = notification.getCreatedAt();
            this.createdAt = notification.getCreatedAt();
        }
    }
}
