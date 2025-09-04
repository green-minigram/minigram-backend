package com.mtcoding.minigram.notifications;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationsController {
    private final NotificationService notificationService;
    private final HttpSession session;

    @GetMapping("/notifications")
    public ResponseEntity<NotificationResponse.ListDTO> findAll(
            @RequestParam Integer viewerId // 로그인 미구현 → 임시
    ) {
        return ResponseEntity.ok(notificationService.findAll(viewerId));
    }
}
