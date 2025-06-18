package com.ssg.chatservice.domain.chat.dto.request;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class ChatDetailRequestDTO {
    private String chatId;
    private String partnerId;
    private String partnerName;
    private String proposalId;
    private NegoStatus negoStatus;
    private ChatStatus chatStatus;
    private String profileImage;
    private String channelName;

}
