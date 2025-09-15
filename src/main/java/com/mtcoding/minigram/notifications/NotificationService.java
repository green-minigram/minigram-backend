package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram._core.config.SseEmitters;
import com.mtcoding.minigram.stories.Story;
import com.mtcoding.minigram.stories.likes.StoryLike;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;

    public NotificationResponse.ListDTO findAllWithinOneMonth(Integer userId) {

        // 1. notification 조회
        List<Object[]> obsList = notificationRepository.findAllByRecipientIdWithinOneMonth(userId);
        if (obsList.isEmpty()) return new NotificationResponse.ListDTO(List.of());

        record NotificationRow(Notification notification, Boolean isFollowing) {
        }

        List<NotificationRow> rows = new ArrayList<>(obsList.size());
        Set<Integer> postLikeIdSet = new HashSet<>();
        Set<Integer> commentIdSet = new HashSet<>();
        Set<Integer> storyLikeIdSet = new HashSet<>();

        // 2. notification, isFollowing -> NotificationRow 조립
        for (Object[] obs : obsList) {
            Notification notification = (Notification) obs[0];
            Boolean isFollowing = (Boolean) obs[1];
            rows.add(new NotificationRow(notification, isFollowing));

            if (notification.getType() == NotificationType.POST_LIKED) {
                postLikeIdSet.add(notification.getTargetId());
            } else if (notification.getType() == NotificationType.COMMENTED) {
                commentIdSet.add(notification.getTargetId());
            } else if (notification.getType() == NotificationType.STORY_LIKED) {
                storyLikeIdSet.add(notification.getTargetId());
            }
        }

        // 3. targetId 기반 타입별 추가 정보 조회
        // 3-1. type = POST_LIKED
        record PostLikeTargetDetail(Integer postId, String postImageUrl) {
        }
        Map<Integer, PostLikeTargetDetail> postLikeDetailMap = new HashMap<>();
        if (!postLikeIdSet.isEmpty()) {
            for (Object[] obs : notificationRepository.findPostLikeTargetDetailsByIds(postLikeIdSet)) {
                Integer targetId = (Integer) obs[0];
                Integer postId = (Integer) obs[1];
                String postImageUrl = (String) obs[2];
                postLikeDetailMap.put(targetId, new PostLikeTargetDetail(postId, postImageUrl));
            }
        }

        // 3-2. type = COMMENTED
        record CommentTargetDetail(Integer postId, String postImageUrl, String commentContent) {
        }
        Map<Integer, CommentTargetDetail> commentDetailMap = new HashMap<>();
        if (!commentIdSet.isEmpty()) {
            for (Object[] obs : notificationRepository.findCommentTargetDetailsByIds(commentIdSet)) {
                Integer targetId = (Integer) obs[0];
                Integer postId = (Integer) obs[1];
                String postImageUrl = (String) obs[2];
                String commentContent = (String) obs[3];
                commentDetailMap.put(targetId, new CommentTargetDetail(postId, postImageUrl, commentContent));
            }
        }

        // 3-3. type = STORY_LIKED
        record StoryLikeTargetDetail(Integer storyId, String storyThumbnailUrl) {
        }
        Map<Integer, StoryLikeTargetDetail> storyLikeDetailMap = new HashMap<>();
        if (!storyLikeIdSet.isEmpty()) {
            for (Object[] obs : notificationRepository.findStoryLikeTargetDetailsByIds(storyLikeIdSet)) {
                Integer targetId = (Integer) obs[0];
                Integer storyId = (Integer) obs[1];
                String storyThumbnailUrl = (String) obs[2];
                storyLikeDetailMap.put(targetId, new StoryLikeTargetDetail(storyId, storyThumbnailUrl));
            }
        }

        // 4. ItemDTO 조립
        List<NotificationResponse.ItemDTO> itemDTOList = new ArrayList<>(rows.size());
        for (NotificationRow row : rows) {
            Notification notification = row.notification();
            Boolean isFollowing = row.isFollowing();

            switch (notification.getType()) {
                case POST_LIKED -> {
                    PostLikeTargetDetail detail = postLikeDetailMap.get(notification.getTargetId());
                    itemDTOList.add(new NotificationResponse.ItemDTO(
                            notification,
                            isFollowing,
                            detail != null ? detail.postId() : null,
                            detail != null ? detail.postImageUrl() : null,
                            null,
                            null,
                            null
                    ));
                }
                case COMMENTED -> {
                    CommentTargetDetail detail = commentDetailMap.get(notification.getTargetId());
                    itemDTOList.add(new NotificationResponse.ItemDTO(
                            notification,
                            isFollowing,
                            detail != null ? detail.postId() : null,
                            detail != null ? detail.postImageUrl() : null,
                            detail != null ? detail.commentContent() : null,
                            null,
                            null
                    ));
                }
                case FOLLOWED -> {
                    itemDTOList.add(new NotificationResponse.ItemDTO(notification, isFollowing, null, null, null, null, null));
                }
                case STORY_LIKED -> {
                    StoryLikeTargetDetail detail = storyLikeDetailMap.get(notification.getTargetId());
                    itemDTOList.add(new NotificationResponse.ItemDTO(
                            notification, isFollowing,
                            null,
                            null,
                            null,
                            detail != null ? detail.storyId() : null,
                            detail != null ? detail.storyThumbnailUrl() : null
                    ));
                }
            }
        }

        return new NotificationResponse.ListDTO(itemDTOList);
    }

    @Transactional
    public void notifyStoryLiked(Story story, StoryLike storyLike, User sender) {
        // 1. 알림 DB 저장
        Notification notificationPS = notificationRepository.save(
                Notification.builder()
                        .type(NotificationType.STORY_LIKED)
                        .sender(sender)               // 좋아요 누른 사람
                        .recipient(story.getUser())   // 스토리 주인
                        .targetId(storyLike.getId())  // story_like.id
                        .status(ReadStatus.UNREAD)
                        .build()
        );

        // 2. SSE 푸시
        NotificationResponse.DTO dto = new NotificationResponse.DTO(notificationPS);

        sseEmitters.sendTo(story.getUser().getId(), "notification", dto);
    }
}
