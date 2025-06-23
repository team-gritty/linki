package com.Gritty.Linki.user.influencer.contract.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.service.AdvertiserContractService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "uCanSignKey=dummy-test-key")
@Transactional
@Log4j2
public class AdContractServiceTest {

    @Autowired
    private AdvertiserContractService adContractService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @BeforeEach
    void setupAuthentication() {
        // 테스트용 사용자 생성
        CustomUserDetails testUser = CustomUserDetails.builder()
                .userId("USER0501") // 실제 DB에 존재하는 테스트 user_id 로 바꿔줘야 함
                .userLoginId("user501")
                .password("123456")
                .build();

        // SecurityContext에 사용자 설정
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(testUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);


    }

    @Test
    void testGetContractsByStatus() {
        // given
        List<ContractStatus> statuses = List.of(ContractStatus.ONGOING, ContractStatus.PENDING_SIGN, ContractStatus.COMPLETED);

        // when
        List<ContractListResponseDTO> result = adContractService.getContractsByStatus(statuses);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty(); // 실제 데이터가 있다면
        result.forEach(contract ->
                log.info("✅ 계약: {} / 상태: {}", contract.getContractId(), contract.getContractStatus()));
    }

    @Test
    @DisplayName("광고주가 본인의 계약 상세정보를 정상적으로 조회한다")
    void testGetContractDetail() {
        // given
        String contractId = "CTR-0000000000000093"; // 실제 DB에 존재하는 contractId로 교체

        // when
        ContractDetailResponseDTO detail = adContractService.getContractDetail(contractId);

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getContractId()).isEqualTo(contractId);

        log.info("📄 계약 ID: {}", detail.getContractId());
        log.info("📄 계약 제목: {}", detail.getContractTitle());
        log.info("📄 캠페인명: {}", detail.getCampaignName());
        log.info("📄 계약 금액: {}", detail.getContractAmount());

        // 예시로 추가 검증
        assertThat(detail.getContractAmount()).isInstanceOf(BigDecimal.class);
        assertThat(detail.getContractStatus()).isNotNull();
    }
}
