package com.mtcoding.minigram.stories.views;

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
        name = "story_views_tb",
        uniqueConstraints = {
                // 같은 유저가 같은 스토리를 여러 번 봐도 행은 1개만 유지
                @UniqueConstraint(columnNames = {"story_id", "user_id"})
        }
)
@Entity
public class StoryView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public StoryView(Integer id, Story story, User user, LocalDateTime createdAt) {
        this.id = id;
        this.story = story;
        this.user = user;
        this.createdAt = createdAt;
    }
}
