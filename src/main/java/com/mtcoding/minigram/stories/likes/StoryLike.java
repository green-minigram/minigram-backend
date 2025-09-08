package com.mtcoding.minigram.stories.likes;

import com.mtcoding.minigram.stories.Story;
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
        name = "story_likes_tb",
        uniqueConstraints = {
                // 한 유저가 같은 스토리에 두 번 좋아요 금지
                @UniqueConstraint(columnNames = {"story_id", "user_id"})
        }
)
@Entity
public class StoryLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public StoryLike(Integer id, Story story, User user, LocalDateTime createdAt) {
        this.id = id;
        this.story = story;
        this.user = user;
        this.createdAt = createdAt;
    }
}
