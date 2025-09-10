package com.mtcoding.minigram.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationResponse.ListDTO findWithinOneMonth(Integer userId) {

        // 1. notification 조회
        List<Object[]> obsList = notificationRepository.findAllByRecipientIdWithinOneMonth(userId);

        // 2. notification, isFollowing으로 itemDTO 1차 조립
        List<NotificationResponse.ItemDTO> itemDTOList = obsList.stream()
                .map(ob -> new NotificationResponse.ItemDTO((Notification) ob[0], (Boolean) ob[1], null, null, null))
                .toList();

        if (itemDTOList.isEmpty()) return new NotificationResponse.ListDTO(itemDTOList);

        // 3. type 별 targetId 리스트 생성 (FOLLOWED 타입일 경우 추가 조회 필요 X)
        List<Integer> postLikeIdList = itemDTOList.stream()
                .filter(dto -> dto.getType() == NotificationType.POST_LIKED)
                .map(dto -> dto.getTargetId())
                .toList();

        List<Integer> commentIdList = itemDTOList.stream()
                .filter(dto -> dto.getType() == NotificationType.COMMENTED)
                .map(dto -> dto.getTargetId())
                .toList();

        // 4. targetId 기반 추가 정보 조회
        // 4-1. type = POST_LIKED
        record PostLikeTargetDetail(Integer postId, String postImageUrl) {
        }

        Map<Integer, PostLikeTargetDetail> postLikeDetailsByTargetId = new HashMap<>();
        if (!postLikeIdList.isEmpty()) {
            for (Object[] obs : notificationRepository.findPostLikeTargetDetailsByIds(postLikeIdList)) {
                Integer targetId = (Integer) obs[0];
                Integer postId = (Integer) obs[1];
                String postImageUrl = (String) obs[2];
                postLikeDetailsByTargetId.put(targetId, new PostLikeTargetDetail(postId, postImageUrl));
            }
        }

        // 4-2. type = COMMENTED
        record CommentTargetDetail(Integer postId, String postImageUrl, String commentContent) {
        }

        Map<Integer, CommentTargetDetail> commentDetailsByTargetId = new HashMap<>();
        if (!commentIdList.isEmpty()) {
            for (Object[] obs : notificationRepository.findCommentTargetDetailsByIds(commentIdList)) {
                Integer targetId = (Integer) obs[0];
                Integer postId = (Integer) obs[1];
                String postImageUrl = (String) obs[2];
                String commentContent = (String) obs[3];
                commentDetailsByTargetId.put(targetId, new CommentTargetDetail(postId, postImageUrl, commentContent));
            }
        }

        // 5. 추가 정보로 itemDTO 2차 조립
        for (NotificationResponse.ItemDTO itemDTO : itemDTOList) {
            if (itemDTO.getType() == NotificationType.POST_LIKED) {
                PostLikeTargetDetail detail = postLikeDetailsByTargetId.get(itemDTO.getTargetId());
                if (detail != null) itemDTO.fillPostLikeDetail(detail.postId(), detail.postImageUrl());
            } else if (itemDTO.getType() == NotificationType.COMMENTED) {
                CommentTargetDetail detail = commentDetailsByTargetId.get(itemDTO.getTargetId());
                if (detail != null)
                    itemDTO.fillCommentDetail(detail.postId(), detail.postImageUrl(), detail.commentContent());
            }
        }

        return new NotificationResponse.ListDTO(itemDTOList);
    }
}
