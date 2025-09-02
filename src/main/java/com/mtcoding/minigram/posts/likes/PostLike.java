package com.mtcoding.minigram.posts.likes;

import com.mtcoding.minigram.posts.Post;
import com.mtcoding.minigram.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Table(
        name = "post_likes_tb",
        uniqueConstraints = {
                // 한 유저가 같은 글에 두 번 좋아요 금지
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        }
)
@Entity
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public PostLike(Integer id, Post post, User user, LocalDateTime createdAt) {
        this.id = id;
        this.post = post;
        this.user = user;
        this.createdAt = createdAt;
    }
}
