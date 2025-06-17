package com.ssg.chatservice.domain.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatCreatEvent {
    private String opponentId;
    private String opponentName;
    private String proposalId;
}
