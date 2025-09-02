package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "notifications_tb")
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition =
            "ENUM('POST_LIKED','COMMENTED','FOLLOWED')")
    private NotificationType type;

    // 알림 발생 주체 (보낸 사람)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    // 알림 수신자 (받는 사람)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_user_id", nullable = false)
    private User recipient;

    // 대상 엔티티 ID (post.id / comment.id / follow.id)
    @Column(name = "target_id", nullable = false)
    private Integer targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('UNREAD','READ') DEFAULT 'UNREAD'")
    private ReadStatus status = ReadStatus.UNREAD;

    // 읽은 시각
    @Column(name = "read_at")
    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Notification(Integer id, NotificationType type, User sender, User recipient, Integer targetId, ReadStatus status, LocalDateTime readAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.sender = sender;
        this.recipient = recipient;
        this.targetId = targetId;
        this.status = status;
        this.readAt = readAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
