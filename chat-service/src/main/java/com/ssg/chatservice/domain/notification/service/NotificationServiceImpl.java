package com.ssg.chatservice.domain.notification.service;

import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.entity.Message;
import com.ssg.chatservice.exception.ChatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final ChatRepository chatRepository;
    private final MessageService messageService;
    // 사용자 ID 기준으로 SseEmitter를 저장하는 맵 (전역 알림용)
    private final Map<String, SseEmitter> userSseEmitters = new ConcurrentHashMap<>();

    @Override
    /**
     * 특정 채팅방에 메시지가 생겼을 때, 전역 SSE를 통해 모든 관련 사용자에게 전송
     */
    public void sendNotificationToChat(String proposalId, String messageContent) {
        String chatId = chatRepository.findByProposalId(proposalId)
                .orElseThrow(()->new ChatException(ErrorCode.CHATROOM_NOT_FOUND))
                .getChatId();

        // 1. 메세지 먼저 저장 (기본은 안읽음)
        Message savedMessage = saveMessage(chatId, messageContent);

        // 2. 전역 SSE를 통해 모든 연결된 사용자에게 알림 전송
        String messageDate = savedMessage.getMessageDate() != null ? 
            savedMessage.getMessageDate().toString() : 
            java.time.LocalDateTime.now().toString();

        log.info("전역 SSE를 통한 채팅방 알림 전송: chatId={}, message={}", chatId, messageContent);
        sendNewMessageNotificationToUser("all", chatId, messageContent, messageDate);
    }




    public Message saveMessage(String chatId, String message) {
        ChatMessageDTO msg = ChatMessageDTO.builder()
                .chatId(chatId)
                .content(message)
                .messageDate(LocalDateTime.now())
                .messageRead(false)
                .senderId("SYSTEM")
                .messageType("NOTIFICATION")
                .build();

        return messageService.saveMessage(msg); // 실제 저장
    }

    /**
     * 사용자별 전역 SSE 구독
     */
    @Override
    public SseEmitter subscribeUser(String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        userSseEmitters.put(userId, emitter);

        // 연결 종료 또는 오류 시 자동 제거
        emitter.onCompletion(() -> {
            log.info("사용자 SSE 연결 완료로 인한 제거: userId={}", userId);
            userSseEmitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            log.info("사용자 SSE 연결 타임아웃으로 인한 제거: userId={}", userId);
            userSseEmitters.remove(userId);
        });
        emitter.onError((e) -> {
            log.error("사용자 SSE 연결 에러로 인한 제거: userId={}, error={}", userId, e.getMessage());
            userSseEmitters.remove(userId);
        });

        log.info("사용자 SSE 구독 완료: userId={}, 총 연결 수: {}", userId, userSseEmitters.size());
        
        // 연결 확인용 초기 메시지 전송
        try {
            emitter.send(SseEmitter.event()
                    .name("CONNECTED")
                    .data("SSE connection established"));
        } catch (IOException e) {
            log.error("초기 연결 메시지 전송 실패: userId={}", userId);
            userSseEmitters.remove(userId);
        }
        
        return emitter;
    }

    /**
     * 사용자에게 새 메시지 알림 전송
     */
    @Override
    public void sendNewMessageNotificationToUser(String userId, String chatId, String content, String messageDate) {
        if ("all".equals(userId)) {
            // 모든 연결된 사용자에게 전송
            userSseEmitters.forEach((connectedUserId, emitter) -> {
                try {
                    String messageData = String.format(
                        "{\"type\":\"NEW_MESSAGE\",\"chatId\":\"%s\",\"content\":\"%s\",\"messageDate\":\"%s\"}",
                        chatId, content, messageDate
                    );
                    
                    emitter.send(SseEmitter.event()
                            .name("NEW_MESSAGE")
                            .data(messageData));

                    log.info("전체 사용자에게 새 메시지 알림 전송 완료: userId={}, chatId={}", connectedUserId, chatId);
                } catch (IOException e) {
                    log.error("전체 사용자 SSE 알림 전송 실패: userId={}, chatId={}", connectedUserId, chatId);
                    userSseEmitters.remove(connectedUserId);
                }
            });
        } else {
            // 특정 사용자에게만 전송
            SseEmitter emitter = userSseEmitters.get(userId);

            if (emitter != null) {
                try {
                    // JSON 형태로 새 메시지 정보 전송
                    String messageData = String.format(
                        "{\"type\":\"NEW_MESSAGE\",\"chatId\":\"%s\",\"content\":\"%s\",\"messageDate\":\"%s\"}",
                        chatId, content, messageDate
                    );
                    
                    emitter.send(SseEmitter.event()
                            .name("NEW_MESSAGE")
                            .data(messageData));

                    log.info("특정 사용자에게 새 메시지 알림 전송 완료: userId={}, chatId={}", userId, chatId);
                } catch (IOException e) {
                    log.error("특정 사용자 SSE 알림 전송 실패: userId={}, chatId={}", userId, chatId);
                    userSseEmitters.remove(userId);
                }
            } else {
                log.info("🚨 [SSE] 사용자 SSE 연결 없음: userId={}, chatId={}", userId, chatId);
            }
        }
    }

    /**
     * 특정 사용자를 제외한 모든 사용자에게 새 메시지 알림 전송
     */
    @Override
    public void sendNewMessageNotificationToAllExcept(String excludeUserId, String chatId, String content, String messageDate) {
        userSseEmitters.forEach((connectedUserId, emitter) -> {
            // 메시지를 보낸 사용자는 제외
            if (!connectedUserId.equals(excludeUserId)) {
                try {
                    String messageData = String.format(
                        "{\"type\":\"NEW_MESSAGE\",\"chatId\":\"%s\",\"content\":\"%s\",\"messageDate\":\"%s\"}",
                        chatId, content, messageDate
                    );
                    
                    emitter.send(SseEmitter.event()
                            .name("NEW_MESSAGE")
                            .data(messageData));

                    log.info("보낸 사람 제외 새 메시지 알림 전송 완료: userId={}, chatId={}, excludeUserId={}", 
                            connectedUserId, chatId, excludeUserId);
                } catch (IOException e) {
                    log.error("보낸 사람 제외 SSE 알림 전송 실패: userId={}, chatId={}", connectedUserId, chatId);
                    userSseEmitters.remove(connectedUserId);
                }
            }
        });
    }

    /**
     * 채팅방 양쪽 참여자에게 전역 SSE를 통한 알림 전송 
     */
    @Override
    public void sendNotificationToBothUsers(String proposalId, String message, String userId1, String userId2) {
        String chatId = chatRepository.findByProposalId(proposalId)
                .orElseThrow(()->new ChatException(ErrorCode.CHATROOM_NOT_FOUND))
                .getChatId();

        // 1. 메세지 먼저 저장 (기본은 안읽음)
        Message savedMessage = saveMessage(chatId, message);

        // 2. 양쪽 사용자 모두에게 전역 SSE 알림 전송
        String messageDate = savedMessage.getMessageDate() != null ? 
            savedMessage.getMessageDate().toString() : 
            java.time.LocalDateTime.now().toString();

        // 현재 연결된 사용자들 로그 출력
        log.info("🔍 [SSE] 현재 연결된 사용자 목록: {}", userSseEmitters.keySet());
        log.info("🔍 [SSE] 총 연결된 사용자 수: {}", userSseEmitters.size());

        // 첫 번째 사용자에게 알림 전송
        if (userId1 != null && !userId1.isEmpty()) {
            log.info("🔔 [SSE] 첫 번째 사용자 알림 시도: userId={}", userId1);
            sendNewMessageNotificationToUser(userId1, chatId, message, messageDate);
            log.info("✅ [SSE] 첫 번째 사용자에게 제안서 알림 전송: userId={}, chatId={}", userId1, chatId);
        }

        // 두 번째 사용자에게 알림 전송
        if (userId2 != null && !userId2.isEmpty()) {
            log.info("🔔 [SSE] 두 번째 사용자 알림 시도: userId={}", userId2);
            sendNewMessageNotificationToUser(userId2, chatId, message, messageDate);
            log.info("✅ [SSE] 두 번째 사용자에게 제안서 알림 전송: userId={}, chatId={}", userId2, chatId);
        }

        log.info("양쪽 사용자에게 제안서 알림 전송 완료: proposalId={}, userId1={}, userId2={}", 
                proposalId, userId1, userId2);
    }

}
