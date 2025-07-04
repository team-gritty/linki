package com.Gritty.Linki.domain.kafka.chat.Event;

import com.Gritty.Linki.domain.kafka.chat.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
    private EventType eventType;
    private String userName;
    private String userId;         // 현재 사용자 ID
    private String proposalId;
    private String userEmail;
    private String partnerUserId;  // 상대방 사용자 ID
    private String partnerEmail;   // 상대방 이메일
}
