package com.mtcoding.minigram.notifications;

import com.mtcoding.minigram._core.config.SseEmitters;
import com.mtcoding.minigram._core.util.Resp;
import com.mtcoding.minigram.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationsController {
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    @GetMapping("/s/api/notifications")
    public ResponseEntity<?> findAllWithinOneMonth(@AuthenticationPrincipal User user) {
        NotificationResponse.ListDTO respDTO = notificationService.findAllWithinOneMonth(user.getId());
        return Resp.ok(respDTO);
    }

    @GetMapping(value = "/s/api/notifications/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@AuthenticationPrincipal User user) {
        // 30분 동안 서버측 응답 없으면, 브라우저에서 자동으로 서버에 재연결 요청을 함

        // 1. 현재 로그인한 사용자 ID 가져오기
        Integer clientId = user.getId();
        log.info("새로고침(clientId) : " + clientId);

        // 2. SseEmitter 객체 생성 (타임아웃: 30분)
        //    - 30분 동안 서버에서 아무 데이터도 안 보내면 자동으로 타임아웃됨
        SseEmitter emitter = new SseEmitter(60 * 1000L * 30);

        // 3. 관리 클래스(sseEmitters)에 emitter 등록
        //    - clientId를 key로 emitter를 저장해둬야, 나중에 알림(push)이 올 때 찾아서 send 가능
        sseEmitters.add(clientId, emitter); // 클라이언트 등록

        try {
            // 4. "첫 이벤트" 전송
            //    - Emitter 생성 후 1분 동안 서버에서 아무 데이터도 보내지 않으면 브라우저가 연결을 끊음
            //    - SSE 프로토콜은 헤더만 받고 body 데이터가 없으면 연결을 유지하지 않음
            //    - 최소 한 번은 body 데이터를 흘려보내야 연결이 "keep-alive" 상태가 됨
            //    - 그렇지 않으면 브라우저가 재연결 시 403 Service Unavailable 발생 가능
            emitter.send(
                    SseEmitter.event()
                            .name("connect") // 이벤트 이름
                            .data("dummy-data")); // 실제 데이터 (여기서는 단순한 더미 값)
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 5. ResponseEntity로 emitter 반환
        //    - produces가 TEXT_EVENT_STREAM이므로 브라우저는 이벤트 스트림을 열고 대기
        return ResponseEntity.ok(emitter);
    }
}
