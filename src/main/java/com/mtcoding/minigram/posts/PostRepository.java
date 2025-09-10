package com.mtcoding.minigram.posts;

import com.mtcoding.minigram.posts.images.PostImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PostRepository {
    private final EntityManager em;

    // 게시글 단건 조회
    public Optional<Post> findById(Integer id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    // 게시글 이미지 목록 조회
    public List<PostImage> findImagesByPostId(Integer postId) {
        return em.createQuery(
                        "select pi from PostImage pi where pi.post.id = :postId",
                        PostImage.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }

    public Integer findAuthorIdByPostId(Integer postId) {
        try {
            return em.createQuery(
                            "select p.user.id from Post p where p.id = :id", Integer.class)
                    .setParameter("id", postId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null; // 못 찾으면 null (isPostAuthor 계산시 false 처리됨)
        }
    }

    public void save(Post post) {
        em.persist(post);
    }

    public Optional<Post> findPostById(Integer id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    public List<Object[]> findFromFollowees(Integer currentUserId) {
        return em.createQuery("""
                            SELECT p,
                               COUNT(DISTINCT pl.id),
                               CASE WHEN EXISTS (SELECT 1 FROM PostLike pl2
                                                  WHERE pl2.post = p AND pl2.user.id = :currentUserId)
                                    THEN true ELSE false END,
                               COUNT(DISTINCT c.id)
                        FROM Post p
                        JOIN FETCH p.user u
                        LEFT JOIN PostLike pl ON pl.post = p
                        LEFT JOIN Comment  c  ON c.post  = p
                        WHERE EXISTS (
                          SELECT 1 FROM Follow f
                           WHERE f.follower.id = :currentUserId AND f.followee = u
                        )
                        GROUP BY p
                        ORDER BY p.createdAt DESC, p.id DESC
                        """, Object[].class)
                .setParameter("currentUserId", currentUserId)
                .getResultList();
    }
}
