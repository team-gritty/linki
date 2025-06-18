package com.ssg.chatservice.domain.chat.dto.respone;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class ChatResponeDTO {
    private String chatId;
    private String opponentId;
    private String opponentName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean isNew;
    private String proposalId;
}
