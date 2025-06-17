package com.ssg.chatservice.domain.chat.controller;

import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatDetailResponeDTO;
import com.ssg.chatservice.domain.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/chat-service/api")
public class ChatController {
    private final ChatServiceImpl chatService;
    private final ModelMapper modelMapper;

    //제안서 아이디를 받아 채팅방 생성
    @GetMapping("/influencer/rooms/{proposalId}")
    public ChatDetailResponeDTO createRoom(@PathVariable String proposalId){
        ChatDetailDTO chatDetailDTO = chatService.createRoom(proposalId);
        return modelMapper.map(chatDetailDTO,ChatDetailResponeDTO.class);
    }

}
