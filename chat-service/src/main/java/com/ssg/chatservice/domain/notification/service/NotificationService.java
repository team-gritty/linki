package com.ssg.chatservice.domain.notification.service;

import com.ssg.chatservice.domain.notification.dto.NotificationDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {

    void sendNotificationToChat(String chatId, String message);
    SseEmitter subscribe(String proposalId);
}
