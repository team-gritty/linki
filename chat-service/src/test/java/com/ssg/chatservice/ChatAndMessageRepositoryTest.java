package com.ssg.chatservice;
import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.domain.message.repository.MessageRepository;
import com.ssg.chatservice.config.MongoConfig;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(MongoConfig.class)
@SpringBootTest
@DisplayName("MongoDB, Mysql(JPA) repository test")
@Rollback(value = false)
@Transactional
public class ChatAndMessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @DisplayName("mongoDB message table 연결 테스트")
    void messageTest(){
        //테스트 메세지 생성
        Message testMessage = Message.builder()
                .messageId("test")
                .chatId("test")
                .messageContent("test")
                .messageDate(Instant.parse("2025-06-16T13:30:00Z"))
                .messageRead(false)
                .messageSenderId("test")
                .messageType("test")
                .build();

        //메시지 저장 후, 저장된 ID로 조회
        Message saved = messageRepository.save(testMessage);
        Optional<Message> result = messageRepository.findById(saved.getMessageId());

        //저장 및 조회 결과 검증
        assertThat(result).isPresent();
        assertThat(result.get().getMessageId()).isEqualTo("test");
        log.info(result.get().toString());
    }

    @Test
    @DisplayName("mysql(Jpa)chat table 연결 테스트")
    void chatTest(){
        Chat chat = Chat.builder()
                .chatDate(Instant.parse("2025-06-16T13:30:00Z"))
                .chatStatus(ChatStatus.PENDING)
                .proposalId("test")
                .build();

        Chat saved = chatRepository.save(chat);
        Optional<Chat> result = chatRepository.findById(saved.getChatId());

        assertThat(result).isPresent();
        assertThat(result.get().getChatId()).isEqualTo(chat.getChatId());
        log.info(result.get().toString());
    }


}

