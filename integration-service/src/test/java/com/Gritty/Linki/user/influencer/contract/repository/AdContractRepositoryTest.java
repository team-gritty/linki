package com.Gritty.Linki.user.influencer.contract.repository;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class AdContractRepositoryTest {

    @Autowired
    private ContractRepository contractRepository;

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


    @Test
    @DisplayName("광고주의 계약 상세 조회")
    void testFindContractDetailForAdvertiser() {
        // given
        String testContractId = "CTR-0000000000000093";     // 실제 존재하는 계약 ID로 대체
        String advertiserId = "ADV0001";        // 실제 존재하는 광고주 ID로 대체

        // when
        Optional<ContractDetailResponseDTO> result =
                contractRepository.findContractDetailForAdvertiser(testContractId, advertiserId);

        // then
        assertThat(result).isPresent();
        ContractDetailResponseDTO dto = result.get();
        log.info("📄 계약 ID: {}", dto.getContractId());
        log.info("📑 계약 제목: {}", dto.getContractTitle());
        log.info("📌 캠페인명: {}", dto.getCampaignName());
        log.info("📎 광고 실행 여부: {}", dto.getAdDeliveryStatus());
        assertThat(dto.getContractId()).isEqualTo(testContractId);
    }

}
