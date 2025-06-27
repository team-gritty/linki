package com.ssg.chatservice.domain.notification.service;

import com.ssg.chatservice.domain.notification.dto.NotificationDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    // 사용자별 전역 SSE 구독
    SseEmitter subscribeUser(String userId);

    // 특정 사용자에게 새 메시지 알림 전송
    void sendNewMessageNotificationToUser(String userId, String chatId, String content, String messageDate);
    
    // 특정 사용자를 제외한 모든 사용자에게 새 메시지 알림 전송
    void sendNewMessageNotificationToAllExcept(String excludeUserId, String chatId, String content, String messageDate);

    // 채팅방 양쪽 참여자에게 알림 전송 (제안서 수정 등)
    void sendNotificationToBothUsers(String proposalId, String message, String userId1, String userId2);
    
    // 특정 채팅방에 메시지가 생겼을 때, 접속 중인 사용자에게 실시간 전송
    void sendNotificationToChat(String proposalId, String messageContent);
}
