package com.ssg.chatservice.domain.message.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
@Builder
public class ChatMessageDTO {
    private String messageId;
    private String chatId;
    private String senderId;
    private String content;
    private String messageType;
    private LocalDateTime messageDate;
    private boolean messageRead;

}
