package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@SpringBootTest
class MessageServiceImplTest {

    @Autowired
    private MessageService messageService;

    @Test
    @DisplayName("MongoDB 메시지 저장 테스트")
    void saveMessage() {
        // given: 저장할 메시지 DTO 생성
        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .chatId("100")
                .messageDate(LocalDateTime.now())  // 현재 시간
                .content("테스트 메시지")
                .messageType("TEXT")
                .senderId("user123")
                .messageRead(false)
                .build();

        // when
        messageService.saveMessage(chatMessageDTO);

        // then
        List<ChatMessageDTO> messages = messageService.findByChatId("100");

        // 메시지가 존재해야 함
        assertThat(messages).isNotEmpty();

        // 방금 저장한 메시지가 포함되어야 함
        ChatMessageDTO saved = messages.stream()
                .filter(msg -> msg.getContent().equals("테스트 메시지") && msg.getSenderId().equals("user123"))
                .findFirst()
                .orElse(null);

        assertThat(saved).isNotNull();
        assertThat(saved.getMessageType()).isEqualTo("TEXT");
        assertThat(saved.isMessageRead()).isFalse();


    }

    @Test
    @DisplayName("MongoDB 채팅 ID로 메시지 조회 테스트")
    void findByChatId() {
        // given: 조회할 채팅 ID
        String chatId = "100";

        // when: 채팅 ID로 메시지 조회
        List<ChatMessageDTO> messages = messageService.findByChatId(chatId);

        // then
        assertThat(messages).isNotNull();    // 리스트가 null이 아님
        assertThat(messages.size()).isGreaterThan(0); // 최소 1개 이상 존재

        ChatMessageDTO first = messages.get(0);
        assertThat(first.getChatId()).isEqualTo(chatId); // chatId 일치 여부

        log.info("조회된 메시지 개수: {}", messages.size());
        for (ChatMessageDTO message : messages) {
            log.info("메시지: {}", message);
        }
    }
}
