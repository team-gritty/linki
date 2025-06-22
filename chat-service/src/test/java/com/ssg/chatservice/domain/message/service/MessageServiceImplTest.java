package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Chat;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
                .chatId("1200")
                .messageDate(LocalDateTime.now())  // 현재 시간
                .content("테스트 메시지")
                .messageType("TEXT")
                .senderId("user123")
                .messageRead(false)
                .build();

        // when
        messageService.saveMessage(chatMessageDTO);

        // then
        List<ChatMessageDTO> messages = messageService.findByChatId("1200");

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

        assertThat(messages).isNotNull();
        assertThat(messages.size()).isGreaterThan(0);

        ChatMessageDTO first = messages.get(0);
        assertThat(first.getChatId()).isEqualTo(chatId);

        log.info("조회된 메시지 개수: {}", messages.size());
        for (ChatMessageDTO message : messages) {
            log.info("메시지: {}", message);
        }
    }
    @Test
    @DisplayName("MongoDB 채팅 ID로 메시지 조회 테스트 - with 저장")
    void findByChatId_withSave() {
        String chatId = "chat-find-test";

        ChatMessageDTO chatMessageDTO = ChatMessageDTO.builder()
                .chatId(chatId)
                .messageDate(LocalDateTime.now())
                .content("조회용 테스트 메시지")
                .messageType("TEXT")
                .senderId("user-test")
                .messageRead(false)
                .build();

        messageService.saveMessage(chatMessageDTO);

        List<ChatMessageDTO> messages = messageService.findByChatId(chatId);

        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getChatId()).isEqualTo(chatId);
    }

    @Test
    @DisplayName("해당 채팅방의 마지막 메세지 조회")
    void lastMessage(){
        List<Chat> chats = List.of(
                new Chat("CHT-0000000000000000", Instant.now(), ChatStatus.PENDING,"PRP-0000000000000000"),
                new Chat("CHT-0000000000000001", Instant.now(), ChatStatus.PENDING,"PRP-0000000000000001")
        );
        ChatMessageDTO chatMessageDTO1 = ChatMessageDTO.builder()
                .chatId("CHT-0000000000000000")
                .messageDate(LocalDateTime.now())
                .content("CHT-0000000000000000의 마지막메시지")
                .messageType("TEXT")
                .senderId("user-test")
                .messageRead(false)
                .build();

        ChatMessageDTO chatMessageDTO2 = ChatMessageDTO.builder()
                .chatId("CHT-0000000000000001")
                .messageDate(LocalDateTime.now())
                .content("CHT-0000000000000001의 마지막메시지")
                .messageType("TEXT")
                .senderId("user-test")
                .messageRead(false)
                .build();

        messageService.saveMessage(chatMessageDTO1);
        messageService.saveMessage(chatMessageDTO2);

        Map<String,ChatMessageDTO> lastMessage = messageService.lastMessage(chats);
        assertThat(lastMessage.get("CHT-0000000000000000").getContent()).isEqualTo(chatMessageDTO1.getContent());
        assertThat(lastMessage.get("CHT-0000000000000001").getContent()).isEqualTo(chatMessageDTO2.getContent());


    }

}
