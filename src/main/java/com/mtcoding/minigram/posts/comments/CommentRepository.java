package com.mtcoding.minigram.posts.comments;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class CommentRepository {
    private final EntityManager em;

    public Map<Integer, Integer> findPostIdMapByCommentIds(List<Integer> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) return Map.of();
        // Comment 엔티티가 c.post.id 로 접근 가능하다는 가정
        String q = "SELECT c.id, c.post.id FROM Comment c WHERE c.id IN :ids";
        List<Object[]> rows = em.createQuery(q, Object[].class)
                .setParameter("ids", commentIds)
                .getResultList();
        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] r : rows) map.put((Integer) r[0], (Integer) r[1]);
        return map;
    }

    public Map<Integer, CommentBrief> findBriefByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Map.of();

        String q = """
                    SELECT c.id, c.post.id, c.content
                    FROM Comment c
                    WHERE c.id IN :ids
                """;
        List<Object[]> rows = em.createQuery(q, Object[].class)
                .setParameter("ids", ids)
                .getResultList();

        Map<Integer, CommentBrief> map = new HashMap<>();
        for (Object[] r : rows) {
            map.put((Integer) r[0],
                    new CommentBrief((Integer) r[1], (String) r[2]));
        }
        return map;
    }

    // 필요한 값만 담는 작은 DTO
    public record CommentBrief(Integer postId, String content) {
    } //todo : record 수정
}
