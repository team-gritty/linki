package com.Gritty.Linki.client.chatClient.controller;

import com.Gritty.Linki.client.chatClient.dto.ChatInfoDto;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/integration-service/api")
public class ChatInfoController {
    private final ChatClientService chatClientService;
    private final ModelMapper modelMapper;

    //파트너 정보 조회
    @GetMapping("/partners/{proposalId}")
    public PartnerInfoResponse getPartnerInfo(@AuthenticationPrincipal CustomUserDetails user, @PathVariable String proposalId) {
        PartnerInfoDto partnerInfoDto = chatClientService.getPartnerByProposal(user,proposalId);
        return modelMapper.map(partnerInfoDto, PartnerInfoResponse.class);
    }

    //캠페인 아이디로 파트너 정보 조회
    @GetMapping("/chatInfo/{campaignId}")
    public List<ChatInfoResponse> getChatInfo(@PathVariable String campaignId) {
        List<ChatInfoDto> chatInfoDtos = chatClientService.getCampaignToChatInfo(campaignId);
        return chatInfoDtos.stream()
                .map(chatInfoDto -> modelMapper.map(chatInfoDto,ChatInfoResponse.class))
                .collect(Collectors.toList());
    }

    //로그인 유저 정보 조회
    @GetMapping("/user-chat")
    public List<ChatInfoResponse> getUserChatInfo(@AuthenticationPrincipal CustomUserDetails user) {
        List<ChatInfoDto> chatInfoDtos = chatClientService.getUserToChatInfo(user);
        return chatInfoDtos.stream()
                .map(chatInfoDto -> modelMapper.map(chatInfoDto,ChatInfoResponse.class))
                .collect(Collectors.toList());
    }

}
