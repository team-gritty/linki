package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService{

    //메세지를 DB에 저장
     Message saveMessage(ChatMessageDTO messageDTO);
     
    //메세지를 DB에 저장하고 발신자에 대해 읽음 처리
     Message saveMessageAndMarkAsReadForSender(ChatMessageDTO messageDTO);

    //해당 채팅창의 모든 메세지 조회 (읽음 처리 포함)
    public List<ChatMessageDTO> findByChatId(String chatId,String userId);
    
    //해당 채팅창의 모든 메세지 조회 (읽음 처리 없음)
    public List<ChatMessageDTO> findByChatIdWithoutMarkingRead(String chatId);
    
    //채팅방 리스트의 마지막 메세지 조회
    Map<String,ChatMessageDTO> lastMessage(List<Chat> chats);
    
    //채팅방 리스트의 마지막 메세지 및 읽지 않은 메시지 여부 조회 (사용자별)
    Map<String,ChatMessageDTO> lastMessageWithUnreadStatus(List<Chat> chats, String userId);
    //채팅창 접속 시 안읽은 메세지 읽음 처리
    void markMessagesAsRead(List<Message> messages, String userId);
    //알람 읽음 처리
    Message notificationMarkAsRead(String messageId);

}
