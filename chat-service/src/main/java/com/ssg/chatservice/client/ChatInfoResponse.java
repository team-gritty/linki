package com.ssg.chatservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatInfoResponse {
    private String opponentId;
    private String opponentName;
    private String proposalId;
}
