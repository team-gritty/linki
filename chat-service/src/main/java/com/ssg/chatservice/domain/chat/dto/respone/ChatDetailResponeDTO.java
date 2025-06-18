package com.ssg.chatservice.domain.chat.dto.respone;

import com.ssg.chatservice.domain.chat.enums.ChatStatus;
import com.ssg.chatservice.domain.chat.enums.NegoStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ChatDetailResponeDTO {
    private String chatId;
    private String partnerId;
    private String partnerName;
    private String proposalId;
    private NegoStatus negoStatus;
    private ChatStatus chatStatus;
    private String profileImage;
    private String channelName;

}
