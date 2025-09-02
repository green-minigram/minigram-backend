package com.mtcoding.minigram.follows;

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
        name = "follows_tb",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
        }
)
@Entity
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 팔로우를 건 사람
    // optional=false → FK(follower_id)는 NULL 허용하지 않음 (필수 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // 팔로우를 당한 사람
    // optional=false → FK(followee_id)는 NULL 허용하지 않음 (필수 관계)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @Builder
    public Follow(Integer id, User follower, User followee, LocalDateTime createdAt) {
        this.id = id;
        this.follower = follower;
        this.followee = followee;
        this.createdAt = createdAt;
    }
}
