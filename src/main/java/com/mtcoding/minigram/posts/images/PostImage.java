package com.mtcoding.minigram.posts.images;

import com.mtcoding.minigram.posts.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "post_images_tb")
@Entity
public class PostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    private Post post;

    @Column(nullable = false)
    private String url;

    @Builder
    public PostImage(Integer id, Post post, String url) {
        this.id = id;
        this.post = post;
        this.url = url;
    }
}
