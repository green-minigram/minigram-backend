package com.mtcoding.minigram.notifications;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationsController {
    private final NotificationService notificationService;
    private final HttpSession session;
}
