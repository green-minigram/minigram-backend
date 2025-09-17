package com.mtcoding.minigram.posts.images;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    public Optional<PostImage> findFirstByPostId(Integer postId) {
        PostImage postImagePS = em.createQuery("""
                        SELECT pi
                        FROM PostImage pi
                        WHERE pi.post.id = :postId
                          AND pi.id = (
                            SELECT MIN(pi2.id)
                            FROM PostImage pi2
                            WHERE pi2.post.id = :postId
                          )
                        """, PostImage.class)
                .setParameter("postId", postId)
                .getSingleResult();

        return Optional.ofNullable(postImagePS);
    }

    public void deleteByPostId(Integer postId) {
        em.createQuery("delete from PostImage pi where pi.post.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}