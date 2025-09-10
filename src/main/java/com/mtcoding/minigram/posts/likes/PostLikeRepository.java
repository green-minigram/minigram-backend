package com.mtcoding.minigram.posts.likes;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PostLikeRepository {
    private final EntityManager em;

    public long countByPostId(Integer postId) {
        var query = "select count(pl) from PostLike pl where pl.post.id = :postId";
        return em.createQuery(query, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    public boolean existsByPostIdAndUserId(Integer postId, Integer userId) {
        var query = "select count(pl) from PostLike pl where pl.post.id = :postId and pl.user.id = :userId";
        Long count = em.createQuery(query, Long.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }

    public void save(PostLike like) {
        em.persist(like);
    }

    public void delete(PostLike like) {
        em.remove(like);
    }
    
    public java.util.Optional<PostLike> findByPostIdAndUserId(Integer postId, Integer userId) {
        var q = """
                select pl from PostLike pl
                where pl.post.id = :postId and pl.user.id = :userId
                """;
        return em.createQuery(q, PostLike.class)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getResultStream()
                .findFirst();
    }

    // (선택) 벌크 삭제 버전
    public int deleteByPostIdAndUserId(Integer postId, Integer userId) {
        var q = """
                delete from PostLike pl
                where pl.post.id = :postId and pl.user.id = :userId
                """;
        return em.createQuery(q)
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .executeUpdate();
    }

}
