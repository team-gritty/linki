package com.ssg.chatservice.domain.message.dto;

import lombok.*;

import java.time.LocalDateTime;
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String messageId;
    private String chatId;
    private String senderId;
    private String content;
    private String messageType;
    private LocalDateTime messageDate;
    private boolean messageRead;

}
