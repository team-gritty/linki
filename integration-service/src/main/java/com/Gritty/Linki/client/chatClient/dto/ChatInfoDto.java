package com.Gritty.Linki.client.chatClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatInfoDto {
    private String opponentId;
    private String opponentName;
    private String proposalId;
}
