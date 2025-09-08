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
    public NotificationResponse.ListDTO findAll(Integer userId) {
        var rows = notificationRepository.findRecentByRecipientId(userId);

        // COMMENT 알림의 commentId 수집
        List<Integer> commentIds = rows.stream()
                .filter(n -> n.getType() == NotificationType.COMMENTED)
                .map(Notification::getTargetId)
                .toList();

        // commentId -> (postId, content) 요약 조회
        Map<Integer, CommentBrief> briefMap = commentRepository.findBriefByIds(commentIds);

        // DTO 변환
        List<NotificationResponse.ItemDTO> items = rows.stream()
                .map(n -> {
                    if (n.getType() == NotificationType.COMMENTED) {
                        CommentBrief b = briefMap.get(n.getTargetId());
                        Long postId = (b == null) ? null : b.getPostId().longValue();
                        String snippet = (b == null) ? null : b.getContent();
                        return NotificationMapper.toItemDTO(n, postId, snippet);
                    }
                    return NotificationMapper.toItemDTO(n);
                })
                .toList();

        return new NotificationResponse.ListDTO(items);
    }
}
