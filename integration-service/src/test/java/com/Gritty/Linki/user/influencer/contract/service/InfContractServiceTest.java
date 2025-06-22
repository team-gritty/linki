package com.Gritty.Linki.user.influencer.contract.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.service.InfluencerContractService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "uCanSignKey=dummy-test-key")
@Transactional
@Log4j2
public class InfContractServiceTest {

    @Autowired
    private InfluencerContractService influencerContractService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @BeforeEach
    void setupAuthentication() {
        // 테스트용 사용자 생성
        CustomUserDetails testUser = CustomUserDetails.builder()
                .userId("USER0000") // 실제 DB에 존재하는 테스트 user_id 로 바꿔줘야 함
                .userLoginId("user0")
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
        List<ContractListResponseDTO> result = influencerContractService.getContractsByStatus(statuses);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty(); // 실제 데이터가 있다면
        result.forEach(contract ->
                log.info("✅ 계약: {} / 상태: {}", contract.getContractId(), contract.getContractStatus()));
    }


}
