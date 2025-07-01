package com.Gritty.Linki.user.influencer.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.CustomUserDetailsService;
import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.proposal.service.InfluencerProposalService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalDetailResponseDTO;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.util.AuthenticationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional

public class ProposalServiceTest {
    @Autowired
    private InfluencerProposalService influencerProposalService;

    @Autowired
    private InfluencerProposalRepository influencerProposalRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    private final String influencerId = "INF0001";
    private final String campaignId = "CAMP0001";

    @Autowired
    private AuthenticationUtil authenticationUtil;


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

    @Test
    @Transactional
    void testUpdateProposal(){
        // 1. 테스트용 유저 생성
        String loginId = "user1";
        CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(loginId);

        //2. 해당 인플루언서가 제출한 제안서 ID 하나 가져오기
        String existingProposalId = "PROP0002";

        //3. 수정 요청 dto 구성
        ProposalRequestDTO dto = new ProposalRequestDTO();
        dto.setContents("수정 내용");

        //4. 실행
        assertDoesNotThrow(() -> influencerProposalService.updateProposal(userDetails, existingProposalId, dto));

        //5. 결과 검증
        Proposal updatedProposal = influencerProposalRepository.findById(existingProposalId)
                .orElseThrow(() -> new RuntimeException("제안서를 찾을 수 없습니다"));

        assertEquals("수정 내용", updatedProposal.getContents());


    }
    @Test
    void testGetProposalDetail(){
        // given
        String logiinId = "user1";
        CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(logiinId);

        String testProoposalId = "PROP0002";

        // when
        ProposalDetailResponseDTO dto = influencerProposalService.getProposalDetail(userDetails,testProoposalId);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getProposalId()).isEqualTo(testProoposalId);
        assertThat(dto.getInfluencerId()).isEqualTo(authenticationUtil.getInfluencerIdFromUserDetails(userDetails));
        assertThat(dto.getContents()).isNotBlank(); // 내용이 있을 경우



    }

    @Test
    @Transactional
    void testDeleteProposal(){
        String loginId = "user1";
        CustomUserDetails userDetails = (CustomUserDetails)userDetailsService.loadUserByUsername(loginId);

        String proposalId = "PROP0002";

        // when & then
        assertDoesNotThrow(() -> {
            influencerProposalService.deleteProposal(userDetails, proposalId);
        });

        // 삭제 이후 확인 (옵션)
        Optional<Proposal> deleted = influencerProposalRepository.findById(proposalId);
        assertThat(deleted).isEmpty();




    }

}
