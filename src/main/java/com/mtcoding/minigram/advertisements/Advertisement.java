package com.mtcoding.minigram.advertisements;

import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "advertisements_tb")
@Entity
public class Advertisement {

    // PK = FK(posts.id). @MapsId로 Post의 id를 그대로 PK로 사용
    @Id
    @Column(name = "post_id")
    private Integer postId;

    // 게시글과 1:1
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    @MapsId // 자식 엔티티의 기본 키(PK)를 부모 엔티티의 기본 키와 공유
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            columnDefinition = "ENUM('ACTIVE','INACTIVE','DELETED') DEFAULT 'ACTIVE'")
    private AdvertisementStatus status = AdvertisementStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Advertisement(Integer postId, Post post, User user, AdvertisementStatus status, LocalDateTime startAt, LocalDateTime endAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.post = post;
        this.user = user;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // @MapsId로 INSERT가 flush까지 지연될 수 있어 save 직후에도 타임스탬프가 null 되지 않도록 라이프사이클에서 직접 세팅.
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
