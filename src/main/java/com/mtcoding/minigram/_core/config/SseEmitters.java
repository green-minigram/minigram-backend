package com.mtcoding.minigram._core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
CopyOnWriteArrayList - 스레드 safe한 List 구현체
ConcurrentHashMap - 스레드 safe한 Map 구현체
내부적으로 모든 쓰기 작업(삽입, 삭제 등)을 수행할 때, 기존 리스트의 복사본을 생성하고 해당 복사본에서 작업을 수행합니다.
이를 통해 읽기와 쓰기 작업이 동시에 발생하더라도 안전하게 동작합니다.
 */

@Slf4j
@Component
public class SseEmitters {

    /*
     * = response들(client의 resonse 정보)
     * array list로 만들면 식별하기 힘듦
     * ConcurrentHashMap = Thread-safe (동시 접근 X)
     */
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    /*
    SseEmitter를 생성할 때는 비동기 요청이 완료되거나 타임아웃 발생 시 실행할 콜백을 등록할 수 있습니다.
    타임아웃이 발생하면 브라우저에서 재연결 요청을 보내는데, 이때 새로운 Emitter 객체를 다시 생성하기 때문에
    기존의 Emitter를 제거해주어야 합니다. 따라서 onCompletion 콜백에서 자기 자신을 지우도록 등록합니다.
     */
    public SseEmitter add(Integer clientId, SseEmitter emitter) {
        // F5를 통한 이미터 재등록시 (기존 이미터를 삭제해야해서 clientId가 필요하다)
        if (emitters.containsKey(clientId)) {
            SseEmitter prevEmitter = emitters.get(clientId);
            prevEmitter.complete();
            log.info("이전 emitter 제거: {}", clientId);
        }
        this.emitters.put(clientId, emitter);
        log.info("새 emitter 클라이언트 ID: {}", clientId);
        log.info("새 emitter 등록 완료: {}", emitter);
        log.info("현재 emitter 개수: {}", emitters.size());

        // SSE 메시지 전송이 완료(onCompletion) 혹은 타임 아웃되면, (onTimeout -> onCompletion)
        // 콜백시에 새로운 스레드에서 emitter를 삭제하기 때문에, 다른 스레드에서 사용중이면 동시성 문제 발생
        // CopyOnWriteArrayList를 사용해서 안전하다.
        emitter.onCompletion(() -> {
            log.info("onCompletion 콜백 실행");
            this.emitters.remove(clientId); // Map.remove(Object key) -> emitter가 아닌 key, clientId를 받아야 한다.
        });
        emitter.onTimeout(() -> {
            log.info("onTimeout 콜백 실행");
            emitter.complete();
        });
        emitter.onError((e) -> {
            log.error("onError: 에러 발생", e);
            emitter.complete();
        });
        return emitter;
    }

    public void sendAll(String eventName, Object data) {
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(  // 각 클라이언트로 SSE 이벤트 푸시
                        SseEmitter.event() // SseEmitter.SseEventBuilder 객체를 생성
                                .name(eventName) // 이벤트 이름
                                .data(data) // 이벤트에 실어보낼 데이터
                );
            } catch (Exception e) {
                log.warn("이벤트 전송 실패, emitter 제거 대상: {}", clientId, e);
                try {
                    emitter.complete();
                } catch (Exception ignored) {
                }
                emitters.remove(clientId);
            }
        });
    }

    // 특정 사용자 한 명에게만 이벤트 전송
    public boolean sendTo(Integer clientId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(clientId); // 대상 emitter 조회
        if (emitter == null) return false;           // 없으면 false
        try {
            emitter.send( // 각 클라이언트로 SSE 이벤트 푸시
                    SseEmitter.event() // SSE 이벤트 메시지를 만들기 위한 빌더 객체를 생성
                            .name(eventName) // 이벤트 이름
                            .data(data) // 이벤트에 실어보낼 데이터
            );
            return true;
        } catch (Exception e) {
            // 실패하면 정리 후 false 처리
            log.warn("클라이언트 {}에게 이벤트 전송(sendTo) 실패 → emitter 제거", clientId, e);
            try {
                emitter.complete();
            } catch (Exception ignored) {
            }
            emitters.remove(clientId);
            return false;
        }
    }
}
