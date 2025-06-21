package com.Gritty.Linki.client.chatClient.service;

import com.Gritty.Linki.client.chatClient.dto.PartnerInfoDto;
import com.Gritty.Linki.config.security.CustomUserDetails;

public interface ChatClientService {
    //권한별 파트너 객체 생성
    PartnerInfoDto getPartnerByProposal(CustomUserDetails user, String proposalId);
    //파트너 아이디 조회
    Object getPartner(CustomUserDetails user, String proposalId);
    //계약 진행 상태 조회
    String getNegoStatust(String proposalId);

}