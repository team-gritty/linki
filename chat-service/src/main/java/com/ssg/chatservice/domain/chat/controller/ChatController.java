package com.ssg.chatservice.domain.chat.controller;

import com.ssg.chatservice.domain.chat.dto.ChatDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatDetailResponeDTO;
import com.ssg.chatservice.domain.chat.dto.respone.ChatResponeDTO;
import com.ssg.chatservice.domain.chat.service.ChatServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat-service/api")
public class ChatController {
    private final ChatServiceImpl chatService;
    private final ModelMapper modelMapper; // PartnerApiClient 주입
//     TODO : 카프카 변경
//    // 제안서 아이디를 받아 채팅방 생성 후 채팅방 아이디 반환
//    @PostMapping("/influencer/rooms/{proposalId}")
//    public ChatResponeDTO createRoom(@PathVariable String proposalId){
//        return  modelMapper.map(chatService.createRoom(proposalId),ChatResponeDTO.class);
//    }

    //TODO : 토큰 전달을 직접하지 않고, jwtutil 사용하여 클라이언트 서비스에서 새로 생성할 것
    //인플루언서의 채팅방 조회
    @GetMapping("/influencer/room/{proposalId}")
    public ChatDetailResponeDTO enterRoomByProposal(@PathVariable String proposalId,
                                                    @RequestHeader("Authorization") String authorizationHeader){
        return modelMapper.map( chatService.getChatDtoByProposalId(authorizationHeader, proposalId), ChatDetailResponeDTO.class);
    }

    //광고주의 채팅목록 조회
    @GetMapping("/advertiser/list/{campaignId}")
    public List<ChatResponeDTO> chatListByCampaign(@RequestHeader("Authorization")String token, @PathVariable String campaignId){
        List<ChatDTO> chats = chatService.campaignToChatList(token,campaignId);
        return chats.stream().map(chatDTO -> { return modelMapper.map(chatDTO,ChatResponeDTO.class);
        }).collect(Collectors.toList());
    }

    //광고주가 채팅목록에서 특정 채팅방 조회
    @GetMapping("/advertiser/chat-detail/{chatId}")
    public ChatDetailResponeDTO chatRoomByCampaign(@RequestHeader("Authorization")String token,@PathVariable String chatId){
        return modelMapper.map(chatService.getChatDtoByChatId(token,chatId),ChatDetailResponeDTO.class);
    }

    //인증유저의 참여 채팅방 조회
    @GetMapping("/authuser/user-chat-list")
    public List<ChatResponeDTO> userChatList(@RequestHeader("Authorization")String token, Principal principal){
        List<ChatDTO> chats = chatService.userToChatList(token, principal.getName());
        return  chats.stream().map(chatDTO -> { return modelMapper.map(chatDTO,ChatResponeDTO.class);
        }).collect(Collectors.toList());
    }

}
