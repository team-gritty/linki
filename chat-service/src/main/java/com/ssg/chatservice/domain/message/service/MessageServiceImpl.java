package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.repository.MessageRepository;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
import com.ssg.chatservice.exception.ChatException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Override
    //메세지를 DB에 저장
    public Message saveMessage(ChatMessageDTO messageDTO){
        Message message = modelMapper.map(messageDTO, Message.class);
        message.initializeIdIfNull();  // ID 자동 생성
        return messageRepository.save(message);
    }

    @Override
    //해당 채팅창의 모든 메세지 조회 (읽음 처리 포함)
    public List<ChatMessageDTO> findByChatId(String chatId,String userId){
        List<Message> messages = messageRepository.findByChatId(chatId);
        //조회한 메세지 읽음 처리
        markMessagesAsRead(messages,userId);
        List<ChatMessageDTO> chatMessages = messages.stream()
                .map(message -> modelMapper.map(message,ChatMessageDTO.class)).collect(Collectors.toList());

        return chatMessages;
    }

    @Override
    //해당 채팅창의 모든 메세지 조회 (읽음 처리 없음)
    public List<ChatMessageDTO> findByChatIdWithoutMarkingRead(String chatId){
        List<Message> messages = messageRepository.findByChatId(chatId);
        // 읽음 처리 없이 조회만
        List<ChatMessageDTO> chatMessages = messages.stream()
                .map(message -> modelMapper.map(message,ChatMessageDTO.class)).collect(Collectors.toList());

        return chatMessages;
    }

    @Override
    //메세지 테이블에서 마지막 메세지 조회(DateType 변환을 위해 DTO로 변환)
    public Map<String,ChatMessageDTO> lastMessage(List<Chat> chats){
        Map<String,ChatMessageDTO> lastMessageMap = new HashMap<>();
        try {
        chats.stream().forEach(chat -> {
            Message lastMessage = messageRepository.findTop1ByChatIdOrderByMessageDateDesc(chat.getChatId());

               ChatMessageDTO chatLastMessageDTO = modelMapper.map(lastMessage, ChatMessageDTO.class);
               lastMessageMap.put(chat.getChatId(), chatLastMessageDTO);
        });
        }catch (Exception  e){
            throw new ChatException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        return lastMessageMap;
    }

    @Override
    //채팅방 리스트의 마지막 메세지 및 읽지 않은 메시지 여부 조회 (사용자별)
    public Map<String,ChatMessageDTO> lastMessageWithUnreadStatus(List<Chat> chats, String userId){
        Map<String,ChatMessageDTO> lastMessageMap = new HashMap<>();
        try {
            chats.stream().forEach(chat -> {
                // 마지막 메시지 조회
                Message lastMessage = messageRepository.findTop1ByChatIdOrderByMessageDateDesc(chat.getChatId());
                
                if (lastMessage != null) {
                    ChatMessageDTO chatLastMessageDTO = modelMapper.map(lastMessage, ChatMessageDTO.class);
                    
                    // 해당 채팅방에서 현재 사용자가 받은 읽지 않은 메시지가 있는지 확인
                    boolean hasUnreadMessages = messageRepository.existsByChatIdAndMessageReadFalseAndMessageSenderIdNot(
                        chat.getChatId(), userId
                    );
                    
                    // isNew 상태를 올바르게 설정
                    chatLastMessageDTO.setMessageRead(!hasUnreadMessages);
                    
                    lastMessageMap.put(chat.getChatId(), chatLastMessageDTO);
                }
            });
        } catch (Exception e) {
            throw new ChatException(ErrorCode.MESSAGE_NOT_FOUND);
        }
        return lastMessageMap;
    }

    @Override
    //메세지 읽음 처리
    public void markMessagesAsRead(List<Message> messages, String userId) {
        List<Message> unreadMessages = messages.stream()
                .filter(m -> !m.isMessageRead())
                .filter(m -> !m.getMessageSenderId().equals(userId))  //내가 보낸 메시지는 제외
                .peek(m -> m.setMessageRead(true))
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            messageRepository.saveAll(unreadMessages);
        }
    }

    //알림 메세지 읽음 처리
    @Override
    public Message notificationMarkAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ChatException(ErrorCode.MESSAGE_NOT_FOUND));

        message.setMessageRead(true);
        return messageRepository.save(message); // 업데이트 반영
    }


}
