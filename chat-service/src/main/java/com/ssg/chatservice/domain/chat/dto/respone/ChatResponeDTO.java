package com.ssg.chatservice.domain.chat.dto.respone;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponeDTO {
    private String chatId;
    private String opponentId;
    private String opponentName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private boolean isNew;
    private String proposalId;
    private String campaignId;
    private ChatStatus chatStatus; // 채팅방 상태 추가
}
