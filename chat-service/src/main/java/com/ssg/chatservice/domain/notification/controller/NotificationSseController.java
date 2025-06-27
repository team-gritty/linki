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
     * 사용자별 전역 SSE 연결 (모든 채팅방의 새 메시지 알림 수신)
     * - 로그인한 사용자가 어떤 페이지에 있든 새 메시지 알림을 받을 수 있음
     */
    @GetMapping(value = "/sse/subscribe/user", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeUser(Principal principal) {
        String userId = principal.getName();
        log.info("사용자 전역 SSE 연결 성공 - userId: {}", userId);
        
        return notificationService.subscribeUser(userId);
    }
}
