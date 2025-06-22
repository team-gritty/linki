package com.Gritty.Linki.client.chatClient.service;

import com.Gritty.Linki.client.chatClient.dto.ChatInfoDto;
import com.Gritty.Linki.client.chatClient.dto.PartnerInfoDto;
import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.domain.user.influencer.contract.UcanSign.UcanSignClient;
import com.Gritty.Linki.entity.Channel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class ChatClientServiceImplTest {

    @Autowired
    private ChatClientServiceImpl chatClientService;
    @MockBean
    private UcanSignClient ucanSignClient;

    @Test
    @DisplayName("인플루언서의 파트너 정보 조회")
    @Transactional
    void getPartnerByProposal_INFLUENCER() {

        String proposalId = "PRP-0000000000000000";
        CustomUserDetails loginUser = CustomUserDetails.builder()
                .userId("USR-0000000000000000")
                .userLoginId("로그인한 인플루언서 유저")
                .password("password")
                .role("ROLE_INFLUENCER")
                .build();

        PartnerInfoDto partnerInfo = chatClientService.getPartnerByProposal(loginUser, proposalId);

        assertThat(partnerInfo).isNotNull();
        assertThat(partnerInfo.getProposalId()).isEqualTo(proposalId);
        log.info("partnerInfo = {}", partnerInfo);
    }

    @Test
    @DisplayName("광고주의 파트너 정보 조회")
    @Transactional
    void getPartnerByProposal_ADVERTISER() {

        String proposalId = "PRP-0000000000000000";
        CustomUserDetails loginUser = CustomUserDetails.builder()
                .userId("USR-0000000000000500")
                .userLoginId("로그인한 광고주 유저")
                .password("password")
                .role("ROLE_ADVERTISER")
                .build();

        PartnerInfoDto partnerInfo = chatClientService.getPartnerByProposal(loginUser, proposalId);

        assertThat(partnerInfo).isNotNull();
        assertThat(partnerInfo.getProposalId()).isEqualTo(proposalId);
        log.info("partnerInfo = {}", partnerInfo);
    }

    //캠페인 아이디로 chatInfo 객체 리스트 조회
    @Test
    @Transactional
    @DisplayName("광고주의 chatInfo 조회 ")
    void  getCampaignToChatInfo(){
        String campaignId = "CMP-0000000000000000";
        List<ChatInfoDto> chatInfos = chatClientService.getCampaignToChatInfo(campaignId);
        chatInfos.forEach(chatInfoDto -> log.info("chatInfoDto = {}",chatInfoDto));
    }

    @Test
    @Transactional
    @DisplayName("로그인 한 광고주의 chatInfo 조회 ")
    void getUserToChatInfoAd(){
        CustomUserDetails loginUser = CustomUserDetails.builder()
                .userId("USR-0000000000000500")
                .userLoginId("로그인한 광고주 유저")
                .password("password")
                .role("ROLE_ADVERTISER")
                .build();

        List<ChatInfoDto> chatInfoDtos = chatClientService.getUserToChatInfo(loginUser);
        chatInfoDtos.forEach(chatInfoDto -> log.info("chatInfoDto = {}",chatInfoDto));
    }
    @Test
    @Transactional
    @DisplayName("로그인 한 인플루언서의 chatInfo 조회 ")
    void getUserToChatInfoInf(){
        CustomUserDetails loginUser = CustomUserDetails.builder()
                .userId("USR-0000000000000000")
                .userLoginId("로그인한 인플루언서 유저")
                .password("password")
                .role("ROLE_INFLUENCER")
                .build();
        List<ChatInfoDto> chatInfoDtos = chatClientService.getUserToChatInfo(loginUser);
        System.out.println("chatInfoDtos.size = " + chatInfoDtos.size());
        assertThat(chatInfoDtos).isNotEmpty();
        chatInfoDtos.forEach(chatInfoDto -> log.info("chatInfoDto = {}",chatInfoDto));
    }



}
