package com.mtcoding.minigram.follows;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
