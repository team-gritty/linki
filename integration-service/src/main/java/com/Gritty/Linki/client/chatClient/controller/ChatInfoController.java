package com.Gritty.Linki.client.chatClient.controller;

import com.Gritty.Linki.client.chatClient.dto.PartnerInfoDto;
import com.Gritty.Linki.client.chatClient.dto.respone.ChatInfoResponse;
import com.Gritty.Linki.client.chatClient.dto.respone.PartnerInfoResponse;
import com.Gritty.Linki.client.chatClient.service.ChatClientService;
import com.Gritty.Linki.config.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/integration-service/api")
public class ChatInfoController {
    private final ChatClientService chatClientService;
    private final ModelMapper modelMapper;

    //파트너 정보 조회
    @GetMapping("/partners/{id}")
    public PartnerInfoResponse getPartnerInfo(@AuthenticationPrincipal CustomUserDetails user, @PathVariable String id) {
        PartnerInfoDto partnerInfoDto = chatClientService.getPartnerByProposal(user,id);
        return modelMapper.map(partnerInfoDto, PartnerInfoResponse.class);
    }

    @GetMapping("/chatInfo/{id}")
    public List<ChatInfoResponse> getChatInfo(@PathVariable String id) {
        return  List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000002"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000003")
        );
    }

    @GetMapping("/user-chat")
    public List<ChatInfoResponse> getUserChatInfo(String authorization) {
        return List.of(
                new ChatInfoResponse("test1", "test1", "PRP-0000000000000002"),
                new ChatInfoResponse("test2", "test2", "PRP-0000000000000003")
        );
    }
}
