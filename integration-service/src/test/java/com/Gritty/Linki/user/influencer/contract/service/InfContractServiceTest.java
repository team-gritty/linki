package com.Gritty.Linki.user.influencer.contract.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.UserRepository;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerUtilRepository;
import com.Gritty.Linki.domain.user.influencer.contract.service.InfluencerContractService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.entity.Influencer;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.repository.InfluencerRepository;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = "uCanSignKey=dummy-test-key")
@Transactional
@Log4j2
public class InfContractServiceTest {


    @Autowired
    private InfluencerUtilRepository influencerUtilRepository;

    @Autowired
    private InfluencerContractService influencerContractService;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setup() {
        // 1. 유저 저장
        User user = userRepository.save(User.builder()
                .userId("USER0000")
                .userLoginId("user0")
                .userLoginPw("123456")
                .userName("사용자0")
                .userEmail("test@example.com")
                .userPhone("010-7113-9755")
                .userRole("ROLE_INFLUENCER")
                .userPayStatus(1)
                .userStatus(1)
                .userEnterDay(LocalDate.of(2024, 10, 15))
                .build());

        // 2. 인플루언서 저장
        Influencer influencer = influencerUtilRepository.save(Influencer.builder()
                .influencerId("INF0000")
                .user(user)
                .build());

        // 3. 인증 설정
        CustomUserDetails testUser = CustomUserDetails.builder()
                .userId(user.getUserId())
                .userLoginId(user.getUserLoginId())
                .password(user.getUserLoginPw())
                .role(user.getUserRole())
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(testUser, null));

        log.info("✅ 테스트 인증 설정 완료: userId = {}, influencerId = {}", user.getUserId(), influencer.getInfluencerId());
    }

    @Test
    @DisplayName("계약 상태별 리스트 조회")
    void testGetContractsByStatus() {
        List<ContractStatus> statuses = List.of(ContractStatus.PENDING_SIGN, ContractStatus.ONGOING, ContractStatus.COMPLETED);

        List<ContractListResponseDTO> result = influencerContractService.getContractsByStatus(statuses);

        assertThat(result).isNotNull();
        result.forEach(dto ->
                log.info("📋 계약 ID: {}, 상태: {}", dto.getContractId(), dto.getContractStatus()));
    }

    @Test
    @DisplayName("인플루언서 계약 상세 조회")
    void testGetContractDetailForInfluencer() {
        // given
        String contractId = "CONT0000"; // 실제 존재하는 계약 ID
        String expectedInfluencerId = "INF0000";

        // when
        ContractDetailResponseDTO result = influencerContractService.getContractDetailForInfluencer(contractId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContractId()).isEqualTo(contractId);
        assertThat(result.getInfluencerId()).isEqualTo(expectedInfluencerId);

        log.info("📄 계약 ID: {}, 인플루언서 ID: {}", result.getContractId(), result.getInfluencerId());
    }


}
