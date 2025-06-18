package com.ssg.chatservice.domain.chat.controller;

import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatDetailResponeDTO;
import com.ssg.chatservice.domain.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat-service/api")
public class ChatController {
    private final ChatServiceImpl chatService;
    private final ModelMapper modelMapper;
    private final PartnerApiClient partnerApiClient;  // PartnerApiClient 주입


    // 제안서 아이디를 받아 채팅방 생성
    @GetMapping("/influencer/rooms/{proposalId}")
    public ChatDetailResponeDTO createRoom(@PathVariable String proposalId,
         @RequestHeader("Authorization") String authorizationHeader){
        ChatDetailDTO chatDetailDTO = chatService.createRoom(authorizationHeader,proposalId);
        PartnerInfoResponse response = partnerApiClient.getPartnerInfo(authorizationHeader, proposalId);

        // 필요하면 PartnerInfoResponse 활용 로직 추가
        return modelMapper.map(chatDetailDTO, ChatDetailResponeDTO.class);
    }

}
