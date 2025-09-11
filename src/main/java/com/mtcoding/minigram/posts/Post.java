package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Table(name = "posts_tb")
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE','HIDDEN','DELETED') DEFAULT 'ACTIVE'")
    private PostStatus status = PostStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC") // 업로드 순서 보장
    private List<PostImage> images = new ArrayList<>();

    public boolean isDeleted() {
        return this.status == PostStatus.DELETED;
    }

    public void markDeleted() {
        if (!isDeleted()) {
            this.status = PostStatus.DELETED; // @UpdateTimestamp 있으면 updatedAt 자동 갱신
        }
    }

    @Builder
    public Post(Integer id, User user, String content, PostStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, List<PostImage> images) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.images = images;
    }
}
