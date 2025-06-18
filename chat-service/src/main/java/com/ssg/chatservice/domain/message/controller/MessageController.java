package com.ssg.chatservice.domain.message.controller;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.dto.request.ChatMessageRequestDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ModelMapper modelMapper;

    //사용자가 보낸 메세지를 해당 채팅방에 전송
    @MessageMapping("/send/message")
    public void sendToUser(@Payload ChatMessageRequestDTO chatMessageRequestDTO, Principal principal){
        // JWT에서 파싱된 인증된 유저 ID
        String senderId = principal.getName();
        ChatMessageDTO chatMessageDTO = modelMapper.map(chatMessageRequestDTO,ChatMessageDTO.class);
        // 클라이언트가 보낸 senderId는 무시하고 서버에서 설정
        chatMessageDTO.setSenderId(senderId);
        //사용자가 보낸 메세지를 해당 채팅방에 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessageDTO.getChatId(),chatMessageDTO);
        //사용자가 보낸 메세지를 DB에 저장
        messageService.saveMessage(chatMessageDTO);
    }

}
