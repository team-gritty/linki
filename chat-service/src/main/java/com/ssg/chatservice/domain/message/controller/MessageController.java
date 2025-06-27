package com.ssg.chatservice.domain.message.controller;

import com.ssg.chatservice.domain.message.dto.ChatMessageDTO;
import com.ssg.chatservice.domain.message.dto.request.ChatMessageRequestDTO;
import com.ssg.chatservice.domain.message.dto.respone.ChatMessageResponeDTO;
import com.ssg.chatservice.domain.message.service.MessageService;
import com.ssg.chatservice.domain.notification.service.NotificationService;
import com.ssg.chatservice.entity.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    //사용자가 보낸 메세지를 해당 채팅방에 전송
    @MessageMapping("send/message")
    public void sendToUser(@Payload ChatMessageRequestDTO chatMessageRequestDTO, Principal principal){
        // JWT에서 파싱된 인증된 유저 ID
        String senderId = principal.getName();
        ChatMessageDTO chatMessageDTO = modelMapper.map(chatMessageRequestDTO,ChatMessageDTO.class);
        // 클라이언트가 보낸 senderId는 무시하고 서버에서 설정
        chatMessageDTO.setSenderId(senderId);
        //사용자가 보낸 메세지를 해당 채팅방에 전송
        messagingTemplate.convertAndSend("/topic/chat/" + chatMessageDTO.getChatId(),chatMessageDTO);
        //사용자가 보낸 메세지를 DB에 저장
        Message savedMessage = messageService.saveMessage(chatMessageDTO);
        // 전역 SSE를 통해 채팅방에 없는 사용자들에게도 새 메시지 알림 전송
        String messageDate = savedMessage.getMessageDate() != null ? 
            savedMessage.getMessageDate().toString() : 
            java.time.LocalDateTime.now().toString();
        notificationService.sendNewMessageNotificationToAllExcept(
            senderId, // 메시지 보낸 사람은 제외
            chatMessageDTO.getChatId(), 
            chatMessageDTO.getContent(), 
            messageDate
        );
    }

    //해당 채팅창의 모든 메세지 조회 (읽음 처리 포함)
    @GetMapping("/v1/chat-service/api/authuser/messages/{chatId}")
    public List<ChatMessageResponeDTO> loadMessages(@PathVariable String chatId,Principal principal) {
        List<ChatMessageDTO> messageDTOs = messageService.findByChatId(chatId,principal.getName());
        return messageDTOs.stream()
                .map(dto -> modelMapper.map(dto, ChatMessageResponeDTO.class))
                .collect(Collectors.toList());
    }

    //해당 채팅창의 모든 메세지 조회 (읽음 처리 없음)
    @GetMapping("/v1/chat-service/api/authuser/messages/{chatId}/without-read")
    public List<ChatMessageResponeDTO> loadMessagesWithoutMarkingRead(@PathVariable String chatId) {
        List<ChatMessageDTO> messageDTOs = messageService.findByChatIdWithoutMarkingRead(chatId);
        return messageDTOs.stream()
                .map(dto -> modelMapper.map(dto, ChatMessageResponeDTO.class))
                .collect(Collectors.toList());
    }

    //채팅방 메시지 읽음 처리
    @PostMapping("/v1/chat-service/api/authuser/messages/{chatId}/mark-read")
    public void markMessagesAsRead(@PathVariable String chatId, Principal principal) {
        List<ChatMessageDTO> messageDTOs = messageService.findByChatIdWithoutMarkingRead(chatId);
        List<Message> messages = messageDTOs.stream()
                .map(dto -> modelMapper.map(dto, Message.class))
                .collect(Collectors.toList());
        messageService.markMessagesAsRead(messages, principal.getName());
    }

}
