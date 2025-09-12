package com.mtcoding.minigram.posts;

import com.mtcoding.minigram._core.constants.UserDetailConstants;
import com.mtcoding.minigram.posts.images.PostImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public boolean existsById(Integer targerId) {
        List<Integer> result = em.createQuery("""
                        select 1
                        from Post p
                        where p.id = :postId
                        """, Integer.class)
                .setParameter("postId", targerId)
                .setMaxResults(1)
                .getResultList();

        return !result.isEmpty();
    }

    public void save(Post post) {
        em.persist(post);
    }

    public Optional<Post> findPostById(Integer id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    public List<PostResponse.SearchItemDTO> findAllByKeyword(int page, String keyword) {
        String sql;

        if (keyword.isBlank()) {
            sql = """
                    SELECT p.id, pi.url, p.content
                    FROM Post p
                    LEFT JOIN PostImage pi
                           ON pi.post = p
                          AND pi.id = (
                              SELECT MIN(pi2.id)
                              FROM PostImage pi2
                              WHERE pi2.post = p
                          )
                    WHERE p.status = :status
                    ORDER BY FUNCTION('RAND')
                    """;
        } else {
            sql = """
                    SELECT p.id, pi.url, p.content
                    FROM Post p
                    LEFT JOIN PostImage pi
                           ON pi.post = p
                          AND pi.id = (
                              SELECT MIN(pi2.id)
                              FROM PostImage pi2
                              WHERE pi2.post = p
                          )
                    WHERE p.status = :status
                        AND p.content LIKE :keyword
                    ORDER BY p.createdAt DESC, p.id DESC
                    """;
        }

        Query query = em.createQuery(sql, Object[].class)
                .setParameter("status", PostStatus.ACTIVE);

        if (!keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        query.setFirstResult(page * 12);
        query.setMaxResults(12);

        List<Object[]> obsList = query.getResultList();

        List<PostResponse.SearchItemDTO> searchItemDTOList = new ArrayList<>();
        for (Object[] obs : obsList) {
            Integer postId = (Integer) obs[0];
            String url = (String) obs[1];
            String content = (String) obs[2];
            searchItemDTOList.add(new PostResponse.SearchItemDTO(postId, url, content));
        }

        return searchItemDTOList;
    }

    public Long totalCountByKeyword(String keyword) {
        String sql = """
                SELECT COUNT(p)
                FROM Post p
                WHERE p.status = :status
                """ + (keyword.isBlank() ? "" : " AND p.content LIKE :keyword");

        TypedQuery<Long> query = em.createQuery(sql, Long.class)
                .setParameter("status", PostStatus.ACTIVE);

        if (!keyword.isBlank()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        return query.getSingleResult();
    }

    public List<Object[]> findAllByUserId(Integer profileUserId, boolean isOwner, Integer page) {
        return em.createQuery("""
                        SELECT p.id, pi.url
                        FROM Post p
                        LEFT JOIN PostImage pi
                               ON pi.post = p
                              AND pi.id = (
                                  SELECT MIN(pi2.id)
                                  FROM PostImage pi2
                                  WHERE pi2.post = p
                              )
                        WHERE p.user.id = :profileUserId
                          AND (
                               p.status = :postActive
                               OR (:isOwner = true AND p.status = :postHidden)
                          )
                        ORDER BY p.createdAt DESC, p.id DESC
                        """, Object[].class)
                .setParameter("profileUserId", profileUserId)
                .setParameter("isOwner", isOwner)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("postHidden", PostStatus.HIDDEN)
                .setFirstResult(page * UserDetailConstants.ITEMS_PER_PAGE)
                .setMaxResults(UserDetailConstants.ITEMS_PER_PAGE)
                .getResultList();
    }

    public Long countAllByUserId(Integer profileUserId, boolean isOwner) {
        return em.createQuery("""
                            SELECT COUNT(p.id)
                            FROM Post p
                            WHERE p.user.id = :profileUserId
                              AND (
                                   p.status = :postActive
                                   OR (:isOwner = true AND p.status = :postHidden)
                              )
                        """, Long.class)
                .setParameter("profileUserId", profileUserId)
                .setParameter("isOwner", isOwner)
                .setParameter("postActive", PostStatus.ACTIVE)
                .setParameter("postHidden", PostStatus.HIDDEN)
                .getSingleResult();
    }
}


