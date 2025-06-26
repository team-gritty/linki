package com.ssg.chatservice.domain.notification.controller;

import com.ssg.chatservice.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat-service/api")
@Slf4j
public class NotificationSseController {

    private final NotificationService notificationService;

    /**
     * 클라이언트가 채팅방에 접속할 때 호출
     * - 서버는 해당 채팅방 ID를 기준으로 실시간 메시지를 전달
     * - 인증은 JwtFilter에서 처리됨 (헤더 또는 쿼리 파라미터)
     */
    @GetMapping(value = "/sse/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String chatId, Principal principal) {
        String userId = principal.getName();
        log.info("SSE 연결 성공 - chatId: {}, userId: {}", chatId, userId);
        
        return notificationService.subscribe(chatId);
    }
}
