package com.mtcoding.minigram.users;

import com.mtcoding.minigram.posts.PostStatus;
import com.mtcoding.minigram.stories.StoryStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final EntityManager em;

    public Optional<User> findByEmail(String email) {
        try {
            User userPS = em.createQuery("select u from User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(userPS);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            User userPS = em.createQuery("select u from User u where u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(userPS);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public User save(User user) {
        em.persist(user);
        return user;
    }

    public Optional<User> findById(Integer userId) {
        return Optional.ofNullable(em.find(User.class, userId));
    }

    public User getReferenceById(Integer userId) {
        return em.getReference(User.class, userId);
    }

    public Optional<UserResponse.ProfileDTO> findProfileById(Integer profileUserId, Integer currentUserId, Boolean isOwner) {

        try {
            Object[] obs = em.createQuery("""
                            SELECT u,
                                   (SELECT COUNT(p.id)
                                                 FROM Post p
                                                WHERE p.user = u
                                                  AND (p.status = :postActive OR (:isOwner = true AND p.status = :postHidden))
                                              ),
                                   (SELECT COUNT(f1.id) FROM Follow f1 WHERE f1.followee = u),
                                   (SELECT COUNT(f2.id) FROM Follow f2 WHERE f2.follower = u),
                                   CASE WHEN EXISTS (
                                       SELECT 1 FROM Follow f
                                        WHERE f.follower.id = :currentUserId AND f.followee = u
                                   ) THEN true ELSE false END,
                                   CASE WHEN EXISTS (
                                       SELECT 1 FROM Story s
                                        WHERE s.user = u AND s.status = :storyActive
                                          AND NOT EXISTS (
                                              SELECT 1 FROM StoryView v
                                               WHERE v.story = s AND v.user.id = :currentUserId
                                          )
                                   ) THEN true ELSE false END
                            FROM User u
                            WHERE u.id = :profileUserId
                            """, Object[].class)
                    .setParameter("profileUserId", profileUserId)
                    .setParameter("currentUserId", currentUserId)
                    .setParameter("isOwner", isOwner)
                    .setParameter("postActive", PostStatus.ACTIVE)
                    .setParameter("postHidden", PostStatus.HIDDEN)
                    .setParameter("storyActive", StoryStatus.ACTIVE)
                    .getSingleResult();

            // 조회 결과 순서 : user, postCount, followerCount, followingCount, isFollowing, hasUnseen
            UserResponse.ProfileDTO profileDTO = new UserResponse.ProfileDTO(
                    (User) obs[0],
                    (Boolean) obs[5],
                    isOwner,
                    (Boolean) obs[4],
                    ((Long) obs[1]).intValue(),
                    ((Long) obs[2]).intValue(),
                    ((Long) obs[3]).intValue()
            );

            return Optional.of(profileDTO);

        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
