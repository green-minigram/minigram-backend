package com.mtcoding.minigram.stories;

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
@Table(name = "stories_tb")
@Entity
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('ACTIVE','HIDDEN','DELETED') DEFAULT 'ACTIVE'")
    private StoryStatus status = StoryStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Story(Integer id, User user, String videoUrl, String thumbnailUrl, StoryStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
