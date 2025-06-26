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
    // 채팅방 ID 기준으로 SseEmitter를 저장하는 맵
    private final Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();


    /**
     * 특정 채팅방에 메시지가 생겼을 때, 접속 중인 사용자에게 실시간 전송
     */
    @Override
    public void sendNotificationToChat(String proposalId, String messageContent) {
        String chatId = chatRepository.findByProposalId(proposalId)
                .orElseThrow(()->new ChatException(ErrorCode.CHATROOM_NOT_FOUND))
                .getChatId();

        // 1. 메세지 먼저 저장 (기본은 안읽음)
        Message savedMessage = saveMessage(chatId, messageContent);

        sseEmitters.keySet().forEach(key -> log.info("등록된 emitter chatId: {}", key));

        SseEmitter emitter = sseEmitters.get(chatId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NOTIFICATION")
                        .data(messageContent));

                log.info("SSE 알림 전송 완료: chatId={}, message={}", chatId, messageContent);
            } catch (IOException e) {
                sseEmitters.remove(chatId);
                throw new ChatException(ErrorCode.SSE_SEND_FAILED);
            }
        } else {
            log.info("SSE 연결 없음: chatId={}, 알림 저장완료 (실시간 전송 x) ", chatId);
        }
    }

    /*
     * 클라이언트가 채팅방에 처음 연결할 때 호출됨
     * - 서버는 해당 채팅방 ID에 대한 emitter를 보관함
     */
    @Override
    public SseEmitter subscribe(String chatId) {

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.put(chatId, emitter);

        // 연결 종료 또는 오류 시 자동 제거
        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료로 인한 제거: chatId={}", chatId);
            sseEmitters.remove(chatId);
        });
        emitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃으로 인한 제거: chatId={}", chatId);
            sseEmitters.remove(chatId);
        });
        emitter.onError((e) -> {
            log.error("SSE 연결 에러로 인한 제거: chatId={}, error={}", chatId, e.getMessage());
            sseEmitters.remove(chatId);
        });

        log.info("SSE 구독 완료: chatId={}, 총 연결 수: {}", chatId, sseEmitters.size());
        return emitter;
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


}
