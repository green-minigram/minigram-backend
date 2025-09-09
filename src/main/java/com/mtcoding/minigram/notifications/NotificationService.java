package com.mtcoding.minigram.notifications;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationResponse.ListDTO findAll(Integer userId) {

        List<Object[]> obsList = notificationRepository.findTop20ByRecipientId(userId);

        List<NotificationResponse.ItemDTO> itemDTOList = obsList.stream().map(ob -> {
            Notification notification = (Notification) ob[0];
            Boolean isFollowing = (Boolean) ob[1];

            NotificationType type = notification.getType();
            Integer targetId = notification.getTargetId();
            // type 에 따라 분기


            return new NotificationResponse.ItemDTO(notification, isFollowing, 1, "", "");
        }).toList();

        return new NotificationResponse.ListDTO(itemDTOList);
    }
}
