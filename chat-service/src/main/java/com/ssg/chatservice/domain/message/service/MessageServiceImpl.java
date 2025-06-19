package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.repository.MessageRepository;
import com.ssg.chatservice.entity.Chat;
import com.ssg.chatservice.entity.Message;
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
    //해당 채팅창의 모든 메세지 조회
    public List<ChatMessageDTO> findByChatId(String chatId){
        List<Message> messages = messageRepository.findByChatId(chatId);
        List<ChatMessageDTO> chatMessages = messages.stream()
                .map(message -> modelMapper.map(message,ChatMessageDTO.class)).collect(Collectors.toList());
        return chatMessages;
    }

    @Override
    //메세지 테이블에서 마지막 메세지 조회(DateType 변환을 위해 DTO로 변환)
    public Map<String,ChatMessageDTO> lastMessage(List<Chat> chats){
        Map<String,ChatMessageDTO> lastMessageMap = new HashMap<>();
        chats.stream().forEach(chat -> {
            Message lastMessage = messageRepository.findTop1ByChatIdOrderByMessageDateDesc(chat.getChatId());
            ChatMessageDTO chatLastMessageDTO = modelMapper.map(lastMessage,ChatMessageDTO.class);
            lastMessageMap.put(chat.getChatId(),chatLastMessageDTO);
        });
        return lastMessageMap;
    }

}
