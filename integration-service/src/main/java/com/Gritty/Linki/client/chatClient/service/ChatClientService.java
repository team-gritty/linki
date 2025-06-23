package com.Gritty.Linki.client.chatClient.service;

import com.Gritty.Linki.client.chatClient.dto.ChatInfoDto;
import com.Gritty.Linki.client.chatClient.dto.InterfaceChatInfoDto;
import com.Gritty.Linki.client.chatClient.dto.PartnerInfoDto;
import com.Gritty.Linki.config.security.CustomUserDetails;

import java.util.List;

public interface ChatClientService {
    //권한별 파트너 객체 생성
    PartnerInfoDto getPartnerByProposal(CustomUserDetails user, String proposalId);
    //파트너 아이디 조회
    Object getPartner(CustomUserDetails user, String proposalId);
    //계약 진행 상태 조회
    String getNegoStatust(String proposalId);
    //캠페인 아이디로 chatInfo 객체 리스트 조회
    List<ChatInfoDto> getCampaignToChatInfo(String campaignId);
    //로그인한 유저의 정보 조회
    List<ChatInfoDto> getUserToChatInfo(CustomUserDetails user);
    //채팅정보 빌더
    List<ChatInfoDto> getChatInfoDtos(List<InterfaceChatInfoDto> interfaceChatInfoDtos);
    }