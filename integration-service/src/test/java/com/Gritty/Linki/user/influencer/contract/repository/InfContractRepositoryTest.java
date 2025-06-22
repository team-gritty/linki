package com.Gritty.Linki.user.influencer.contract.repository;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class InfContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @DisplayName("인플루언서 ID와 상태 리스트로 계약 목록 조회")
    void testFindContractsByInfluencerIdAndStatus() {
        // given
        String influencerId = "INF0000"; // 실제 존재하는 ID로 대체
        List<ContractStatus> statuses = Arrays.asList(
                ContractStatus.PENDING_SIGN,
                ContractStatus.ONGOING,
                ContractStatus.COMPLETED
        );

        // when
        List<ContractListResponseDTO> contracts = contractRepository
                .findContractsByInfluencerIdAndStatus(influencerId, statuses);

        // then
        assertThat(contracts).isNotNull();
        log.info("조회된 계약 수: {}", contracts.size());
        contracts.forEach(contract -> {
            log.info("📄 계약 ID: {}, 상태: {}", contract.getContractId(), contract.getContractStatus());
            assertThat(statuses).contains(contract.getContractStatus());
        });
    }


    @Test
    @DisplayName("광고주의 계약 목록 조회")
    void findContractsByAdvertiserIdAndStatusTest() {
        // given
        String advertiserId = "ADV0001"; // 테스트용 더미 광고주 ID
        List<ContractStatus> statuses = List.of(ContractStatus.ONGOING, ContractStatus.COMPLETED, ContractStatus.PENDING_SIGN);

        // when
        List<ContractListResponseDTO> result = contractRepository.findContractsByAdvertiserIdAndStatus(advertiserId, statuses);

        // then
        assertThat(result).isNotNull();
        log.info("조회된 계약 수: {}", result.size());
        result.forEach(contract -> {
            log.info("계약 ID: {}, 상태: {}", contract.getContractId(), contract.getContractStatus());
            assertThat(statuses).contains(contract.getContractStatus());
        });
    }


}
