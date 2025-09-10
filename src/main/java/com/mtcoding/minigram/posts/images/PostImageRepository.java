package com.mtcoding.minigram.posts.images;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostImageRepository {

    private final EntityManager em;

    public void save(PostImage postImage) {
        em.persist(postImage);
    }
}