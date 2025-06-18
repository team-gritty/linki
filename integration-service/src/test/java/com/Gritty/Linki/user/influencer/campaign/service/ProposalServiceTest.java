package com.Gritty.Linki.user.influencer.campaign.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.CustomUserDetailsService;
import com.Gritty.Linki.domain.user.advertiser.repository.jpa.AdvertiserRepository;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerUtilRepository;
import com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa.InfluencerProposalRepository;
import com.Gritty.Linki.domain.user.influencer.proposal.service.InfluencerProposalService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalListResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalResponseDTO;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.util.AuthenticationUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static com.Gritty.Linki.vo.enums.Category.VLOG;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class ProposalServiceTest {
    @Autowired
    private InfluencerUtilRepository influencerUtilRepository;

    @Autowired
    private InfluencerProposalService influencerProposalService;

    @Autowired
    private InfluencerProposalRepository influencerProposalRepository;

    @Autowired
    private InfluencerCampaignRepository influencerCampaignRepository;

    @Autowired
    private AdvertiserRepository advertiserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired private AuthenticationUtil authenticationUtil;





    Influencer testInfluencer;
    Campaign testCampaign;


    @BeforeEach
    void setUp() {

        // 광고주 저장
        Advertiser advertiser = advertiserRepository.save(
                Advertiser.builder()
                        .advertiserId("ADV0001")
                        .userId("USER0501") // userId는 임의값
                        .businessNumber("123-45-0001")
                        .companyName("회사1")

                        .build()
        );
        testInfluencer = Influencer.builder()
                .influencerId("INF0001")
                .userId("USER0001")  // userId를 나중에 조회 키로 사용
                .build();

        testCampaign =    Campaign.builder()
                .campaignId("CAMP0001")
                .campaignName("캠페인1")
                .advertiser(advertiser)
                .campaignImg("https://example.com/images/0.jpg")
                .createdAt(LocalDateTime.of(2024, 6, 3, 0, 0))
                .campaignDeadline(LocalDateTime.of(2025, 1, 9, 0, 0))
                .campaignCategory(VLOG)
                .build();

    }



    @Test
    void testSubmitProposal() {


        // 2) 실제 있는 유저로 UserDetails 로드 (DB에 연관된 사용자 계정이 미리 있어야 합니다)
        CustomUserDetails user = (CustomUserDetails) customUserDetailsService.loadUserByUsername("user1");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
        );

        // 3) 요청 생성
        ProposalRequestDTO req = new ProposalRequestDTO(
                testCampaign.getCampaignId(),
                "실제 DB 연동 테스트용 제안 내용"
        );

        // 4) 서비스 호출
        ProposalResponseDTO res = influencerProposalService.submitProposal(user,req);

        // 5) DB에 제대로 저장됐는지 확인
        Proposal saved = influencerProposalRepository.findById(res.getProposalId())
                .orElseThrow(() -> new AssertionError("제안이 DB에 저장되지 않았습니다"));

        assertThat(saved.getContents())
                .isEqualTo("실제 DB 연동 테스트용 제안 내용");
        assertThat(saved.getInfluencer().getInfluencerId())
                .isEqualTo(testInfluencer.getInfluencerId());
        assertThat(saved.getCampaign().getCampaignId())
                .isEqualTo(testCampaign.getCampaignId());
        assertThat(res.getSubmittedAt())
                .isNotNull();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext(); // 💡 전역 컨텍스트 정리
    }

    @Test
    void testGetMyProposals() {
        // 1) UserDetails 준비 (실제 DB에서 로드)
        CustomUserDetails userDetails =
                (CustomUserDetails) customUserDetailsService.loadUserByUsername("user1");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // 2) 서비스 호출
        List<ProposalListResponseDTO> dtoList =
                influencerProposalService.getMyProposals(userDetails);

        // 3) 기본 검증: null/empty 아님
        assertThat(dtoList)
                .as("인플루언서가 제출한 제안 목록이 비어 있으면 안 된다")
                .isNotNull()
                .isNotEmpty();

        // 4) 모든 DTO.influencerId 가 로그인한 인플루언서와 일치하는지
        String expectedInfluencerId =
                authenticationUtil.getInfluencerIdFromUserDetails(userDetails);
        assertThat(dtoList)
                .as("모든 제안의 influencerId는 로그인한 인플루언서(%s)와 같아야 한다",
                        expectedInfluencerId)
                .allMatch(dto -> dto.getInfluencerId().equals(expectedInfluencerId));
    }
}
