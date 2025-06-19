package com.ssg.chatservice.domain.chat.controller;

import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.dto.ChatDetailDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatDetailResponeDTO;
import com.ssg.chatservice.domain.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat-service/api")
public class ChatController {
    private final ChatServiceImpl chatService;
    private final ModelMapper modelMapper;
    private final PartnerApiClient partnerApiClient;  // PartnerApiClient 주입

    // 제안서 아이디를 받아 채팅방 생성 후 채팅방 아이디 반환
    @PostMapping("/influencer/rooms/{proposalId}")
    public String createRoom(@RequestBody String proposalId){
        return  chatService.createRoom(proposalId);
    }

    //TODO : 토큰 전달을 직접하지 않고, jwtutil 사용하여 클라이언트 서비스에서 새로 생성할 것
    //인플루언서의 채팅방 조회
    @GetMapping("/influencer/room/{proposalId}")
    public ChatDetailResponeDTO enterRoomByProposal(@PathVariable String proposalId,
                                                    @RequestHeader("Authorization") String authorizationHeader){
        ChatDetailDTO chatDetailDTO = chatService.findByProposalId(authorizationHeader, proposalId);
        return modelMapper.map(chatDetailDTO, ChatDetailResponeDTO.class);
    }

}
