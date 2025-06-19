package com.ssg.chatservice.domain.message.service;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.repository.MessageRepository;
import com.ssg.chatservice.entity.Message;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    //메세지를 DB에 저장
    public Message saveMessage(ChatMessageDTO messageDTO){
        Message message = modelMapper.map(messageDTO, Message.class);
        message.initializeIdIfNull();  // ID 자동 생성
        return messageRepository.save(message);
    }

    //해당 채팅창의 모든 메세지 조회
    public List<ChatMessageDTO> findByChatId(String chatId){
        List<Message> messages = messageRepository.findByChatId(chatId);
        List<ChatMessageDTO> chatMessages = messages.stream()
                .map(message -> modelMapper.map(message,ChatMessageDTO.class)).collect(Collectors.toList());
        return chatMessages;
    }
}
