package com.mtcoding.minigram.posts.comments;

import com.mtcoding.minigram.posts.Post;
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
@Table(name = "comments_tb")
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    // 부모 댓글 (NULL 가능 → 최상위 댓글, 값 있으면 대댓글)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE','DELETED') DEFAULT 'ACTIVE'")
    private CommentStatus status = CommentStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 대댓글 리스트
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Comment> children = new ArrayList<>();

    @Builder
    public Comment(Integer id, Post post, User user, Comment parent, String content, CommentStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, List<Comment> children) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.parent = parent;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.children = children;
    }

    public boolean isDeleted() {
        return this.status == CommentStatus.DELETED;
    }

    public void markDeleted() {
        if (isDeleted()) return;
        this.status = CommentStatus.DELETED;
    }
}
