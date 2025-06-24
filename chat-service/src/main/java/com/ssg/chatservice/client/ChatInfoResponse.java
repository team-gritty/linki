package com.ssg.chatservice.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatInfoResponse {
    private String opponentId;
    private String opponentName;
    private String proposalId;
    private String campaignId;
}
