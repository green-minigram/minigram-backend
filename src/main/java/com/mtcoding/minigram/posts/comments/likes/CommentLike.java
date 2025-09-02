package com.mtcoding.minigram.posts.comments.likes;

import com.mtcoding.minigram.posts.comments.Comment;
import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(name = "comment_likes_tb", uniqueConstraints = {
        // 한 유저가 같은 댓글에 두 번 좋아요 금지
        @UniqueConstraint(columnNames = {"comment_id", "user_id"})
})
@Entity
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public CommentLike(Integer id, Comment comment, User user, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.user = user;
        this.createdAt = createdAt;
    }
}
