package com.Gritty.Linki.user.influencer.review.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.review.repository.jpa.InfAdvertiserReviewRepository;
import com.Gritty.Linki.domain.user.influencer.review.service.InfluencerReviewService;
import com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa.InfSettlementRepository;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "uCanSignKey=dummy-test-key")
@Transactional
@Log4j2
public class InfReviewServiceTest {
    @Autowired
    private InfluencerReviewService influencerReviewService;

    @Autowired
    private InfAdvertiserReviewRepository reviewRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private InfSettlementRepository settlementRepository;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Test
    void createReviewTest() throws Exception {
        // given - 우리 DB에 존재하는 완료된 contractId로 테스트
        String contractId = "CONT0001"; // 실제 존재하는 contract_id로 변경 필요

        // ✅ SecurityContext에 로그인 유저 설정
        CustomUserDetails mockUser = new CustomUserDetails("USER0000", "user0", List.of().toString(), "ROLE_INFLUENCER"); // influencer-id는 계약과 연결된 인플루언서의 ID여야 함
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ request 구성
        InfAdvertiserReviewRequestDTO request = new InfAdvertiserReviewRequestDTO();
        request.setContractId(contractId);
        request.setAdvertiserReviewScore(BigDecimal.valueOf(4.5));
        request.setAdvertiserReviewComment("테스트 리뷰입니다.");

        // when
        influencerReviewService.submitAdvertiserReview(request);

        // then
        boolean exists = reviewRepository.existsByContract_ContractId(contractId);
        assertThat(exists).isTrue();
    }

    @Test
    void getReviewableContractsTest(){

            // given: SecurityContext에 인증 정보 수동 삽입
            CustomUserDetails userDetails = CustomUserDetails.builder()
                    .userId("USER0000")  // ← 실제 존재하는 인플루언서의 userId
                    .role("ROLE_INFLUENCER")
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // when
            List<ReviewableContractResponseDTO> result = influencerReviewService.getReviewableContracts();

            // then
            assertThat(result).isNotNull();
            assertThat(result).allSatisfy(dto -> {
                assertThat(dto.getSettlementStatus()).isEqualTo(SettlementStatus.COMPLETED);
                assertThat(dto.getContractStatus()).isEqualTo(ContractStatus.COMPLETED);
            });

            result.forEach(dto ->
                    log.info("✅ 계약 ID: " + dto.getContractId() +
                            ", 캠페인 제목: " + dto.getCampaignTitle() +
                            ", 금액: " + dto.getContractAmount())
            );

            // cleanup
            SecurityContextHolder.clearContext();


    }

}
