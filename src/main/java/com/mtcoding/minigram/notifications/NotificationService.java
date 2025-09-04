package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram.posts.comments.CommentRepository;
import com.mtcoding.minigram.posts.comments.CommentRepository.CommentBrief;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

// @Slf4j
// - Lombok이 자동으로 Logger 필드를 추가해주는 어노테이션
// - log.info()/debug()/error()
@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public NotificationResponse.ListDTO findAll(Integer viewerId) {
        var rows = notificationRepository.findRecent20ByRecipientId(viewerId.longValue());

        // COMMENT 알림의 commentId 수집
        List<Integer> commentIds = rows.stream()
                .filter(n -> n.getType() == NotificationType.COMMENTED)
                .map(Notification::getTargetId) // = commentId
                .toList();

        // commentId -> postId 매핑 조회
        Map<Integer, Integer> commentToPost = commentRepository.findPostIdMapByCommentIds(commentIds);

        Map<Integer, CommentBrief> cBrief = commentRepository.findBriefByIds(commentIds);


        // DTO 변환 (댓글이면 postId 주입)
        List<NotificationResponse.ItemDTO> items = rows.stream()
                .map(n -> {
                    if (n.getType() == NotificationType.COMMENTED) {
                        CommentBrief b = cBrief.get(n.getTargetId());
                        Long postId = b == null ? null : asLong(b.postId());
                        String snippet = b == null ? null : b.content();
                        return NotificationMapper.toItemDTO(n, postId, snippet);
                    }
                    return NotificationMapper.toItemDTO(n); // 좋아요/팔로우
                })
                .toList();

        return new NotificationResponse.ListDTO(items);
    }

    private static Long asLong(Integer i) {
        return i == null ? null : i.longValue();
    }
}
