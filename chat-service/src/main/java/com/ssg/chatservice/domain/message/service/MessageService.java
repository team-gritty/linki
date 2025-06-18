package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Message;

import java.util.List;

public interface MessageService{

    //메세지를 DB에 저장
     Message saveMessage(ChatMessageDTO messageDTO);

    //해당 채팅창의 모든 메세지 조회
    List<ChatMessageDTO> findByChatId(String chatId);
}
