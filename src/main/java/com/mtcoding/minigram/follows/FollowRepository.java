package com.mtcoding.minigram.follows;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FollowRepository {

    private final EntityManager em;

    //팔로우 여부
    public boolean existsByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId) {
        Long count = em.createQuery(
                        "select count(f) from Follow f " +
                                "where f.follower.id = :followerId and f.followee.id = :followeeId",
                        Long.class
                )
                .setParameter("followerId", followerId)
                .setParameter("followeeId", followeeId)
                .getSingleResult();

        return count != null && count > 0;
    }

    public Follow save(Follow follow) {
        em.persist(follow);
        return follow;
    }

    public Optional<Follow> findByFollowerIdAndFolloweeId(Integer followerId, Integer followeeId) {
        try {
            Follow followPS = em.createQuery("select f from Follow f where f.follower.id = :followerId and f.followee.id = :followeeId", Follow.class)
                    .setParameter("followerId", followerId)
                    .setParameter("followeeId", followeeId)
                    .getSingleResult();
            return Optional.of(followPS);
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    public void deleteById(Integer followId) {
        em.createQuery("delete from Follow f where f.id = :followId")
                .setParameter("followId", followId)
                .executeUpdate();
    }

    // 팔로우 목록
    public List<Object[]> findFollowersByFolloweeId(Integer currentUserId, Integer followeeId) {
        return em.createQuery("""
                            SELECT u,
                                   CASE WHEN COUNT(f2) > 0 THEN true ELSE false END
                            FROM Follow f
                              JOIN f.follower u
                              LEFT JOIN Follow f2
                                     ON f2.follower.id = :currentUserId
                                    AND f2.followee.id = u.id
                            WHERE f.followee.id = :followeeId
                            GROUP BY u
                            ORDER BY MAX(f.id) DESC
                        """, Object[].class)
                .setParameter("followeeId", followeeId)
                .setParameter("currentUserId", currentUserId)
                .getResultList();
    }

    // 팔로잉 목록
    public List<Object[]> findFollowingByFollowerId(Integer currentUserId, Integer followerId) {
        return em.createQuery("""
                            SELECT u,
                                   CASE WHEN COUNT(f2) > 0 THEN true ELSE false END
                            FROM Follow f
                              JOIN f.followee u
                              LEFT JOIN Follow f2
                                     ON f2.follower.id = :currentUserId
                                    AND f2.followee.id = u.id
                            WHERE f.follower.id = :followerId
                            GROUP BY u
                            ORDER BY MAX(f.id) DESC
                        """, Object[].class)
                .setParameter("followerId", followerId)
                .setParameter("currentUserId", currentUserId)
                .getResultList();
    }
}
