package com.Gritty.Linki.user.influencer.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerUtilRepository;
import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.proposal.service.InfluencerProposalService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.entity.Proposal;
import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional

public class ProposalServiceTest {
    @Autowired
    private InfluencerProposalService influencerProposalService;

    @Autowired
    private InfluencerProposalRepository influencerProposalRepository;

    private final String influencerId = "INF0001";
    private final String campaignId = "CAMP0001";

    @BeforeEach
    void setUp() {
        // 가짜 사용자 정보 생성
        CustomUserDetails userDetails = new CustomUserDetails(
                "USER0001", "user1", "password", "ROLE_INFLUENCER"
        );

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // SecurityContext에 수동으로 세팅
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }



    @Test
    void testSubmitProposal(){


        ProposalRequestDTO dto = new ProposalRequestDTO();
        dto.setCampaignId(campaignId);
        dto.setContents("이 캠페인에 꼭 참여하고 싶습니다");


        //when
       influencerProposalService.submitProposal(
               (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
               dto
       );

        //then
        List<Proposal> proposals = influencerProposalRepository.findAll();
        assertThat(proposals).isNotEmpty();

        Proposal recent = proposals.get(proposals.size() - 1);
        assertThat(recent.getCampaign().getCampaignId()).isEqualTo(campaignId);
        assertThat(recent.getInfluencer().getInfluencerId()).isEqualTo(influencerId);
        assertThat(recent.getContents()).isEqualTo(dto.getContents());
        assertThat(recent.getSubmittedAt()).isNotNull();

        System.out.println("✅ 등록된 제안서 ID: " + recent.getProposalId());
        System.out.println("✅ 내용: " + recent.getContents());
        System.out.println("✅ 제출 시각: " + recent.getSubmittedAt());


    }

}
