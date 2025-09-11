package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationsController {
    private final NotificationService notificationService;

    @GetMapping("/s/api/notifications")
    public ResponseEntity<?> findAllWithinOneMonth(@AuthenticationPrincipal User user) {
        NotificationResponse.ListDTO respDTO = notificationService.findAllWithinOneMonth(user.getId());
        return Resp.ok(respDTO);
    }
}
