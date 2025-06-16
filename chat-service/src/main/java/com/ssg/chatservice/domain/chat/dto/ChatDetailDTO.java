package com.ssg.chatservice.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChatDetailDTO {
    private String chatId;
    private String partnerId;
    private String partnerName;
    private String proposalId;
    private String negoStatus;
    private String chatStatus;
    private String profileImage;
    private String channelName;

}
