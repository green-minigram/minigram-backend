package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    public Optional<Post> findById(Integer id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    public List<PostImage> findImagesByPostId(Integer postId) {
        var jpql = "select pi from PostImage pi where pi.post.id = :postId";
        return em.createQuery(jpql, PostImage.class)
                .setParameter("postId", postId)
                .getResultList();
    }
}
