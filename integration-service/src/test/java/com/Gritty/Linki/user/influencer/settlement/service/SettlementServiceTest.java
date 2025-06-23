package com.Gritty.Linki.user.influencer.settlement.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;
import com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa.InfSettlementRepository;
import com.Gritty.Linki.domain.user.influencer.settlement.service.InfluencerSettlementService;
import com.Gritty.Linki.domain.user.influencer.settlement.service.InfluencerSettlementServiceImpl;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = "uCanSignKey=dummy-test-key")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Log4j2
public class SettlementServiceTest {
    @Autowired
    private InfluencerSettlementService influencerSettlementService;

    @BeforeEach
    void setUpAuthentication() {
        CustomUserDetails user =
                new CustomUserDetails("USER0000", "user0", "123456", "ROLE_INFLUENCER");
        Authentication auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetCompletedSettlementsForInfluencer() {

        // when
        List<SettlementResponseDTO> result = influencerSettlementService.getCompletedSettlementsForInfluencer();

        // then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty(); // 또는 .hasSize(x)
        assertThat(result.get(0).getSettlementStatus()).isEqualTo(SettlementStatus.COMPLETED);

        // 확인용 로그
        result.forEach(dto -> log.info("✅ 정산 ID: " + dto.getSettlementId()));
    }
}
