package com.Gritty.Linki.client.chatClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerInfoDto {
    private String partnerId;
    private String partnerName;
    private String proposalId;
    private String profileImage;
    private String channelName;
    private String negoStatus;
}
