package com.mtcoding.minigram.posts.images;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostImageRepository {

    private final EntityManager em;

    public void save(PostImage postImage) {
        em.persist(postImage);
    }

    public List<PostImage> findAllByPostIdIn(List<Integer> postIdList) {
        return em.createQuery("""
                select pi
                from PostImage pi
                where pi.post.id in :postIdList
                ORDER BY pi.post.id ASC, pi.id ASC
            """, PostImage.class)
                .setParameter("postIdList", postIdList)
                .getResultList();
    }
}