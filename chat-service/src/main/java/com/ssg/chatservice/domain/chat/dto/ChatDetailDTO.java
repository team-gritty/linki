package com.ssg.chatservice.domain.chat.dto;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDetailDTO {
    private String chatId;
    private String partnerId;
    private String partnerName;
    private String proposalId;
    private NegoStatus negoStatus;
    private ChatStatus chatStatus;
    private String profileImage;
    private String channelName;

}
