package com.Gritty.Linki.client.chatClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface InterfaceChatInfoDto {
    String getUserId();
    String getUserLoginId();
    String getProposalId();
    String getCampaignId();
}
