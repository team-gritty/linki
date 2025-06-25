package com.Gritty.Linki.user.influencer.contract.repository;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.vo.enums.Category;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class AdContractRepositoryTest {

    @PersistenceContext
    private EntityManager em;

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

    @Test
    void  changeAddeliveryStatusTest(){
        // given

        Advertiser advertiser = Advertiser.builder()
                .advertiserId("ADV0001")
                .businessNumber("34245")
                .companyName("회사1")
                .userId("USER0501")
                .build();


        Campaign campaign = Campaign.builder()
                .campaignId("CAMP0001")
                .campaignName("캠페인1")
                .campaignImg("이미지")
                .createdAt(LocalDateTime.now())
                .campaignDeadline(LocalDateTime.now())
                .campaignCategory(Category.EDUCATION)
                .advertiser(advertiser)
                .build();

        Influencer influencer = Influencer.builder()
                .influencerId("INF0001")
                .userId("USER0001")
                .build();



        Proposal proposal = Proposal.builder()
                .proposalId("PROP0001")
                .status(ProposalStatus.ACCEPTED)
                .submittedAt(LocalDateTime.now())
                .respondedAt(LocalDateTime.now())
                .influencer(influencer)
                .campaign(campaign)
                .build();


        Contract contract = Contract.builder()
                .contractStatus(ContractStatus.COMPLETED)
                .contractTitle("테스트")
                .contractId("cont23")
                .contractCompletedAt(LocalDateTime.now())
                .contractSpecialTerms("특약")
                .contractAmount(BigDecimal.valueOf(100000))
                .contractCreatedAt(LocalDateTime.now())
                .contractStartDate(LocalDate.from(LocalDateTime.now()))
                .contractEndDate(LocalDate.from(LocalDateTime.now()))
                .documentId("docu")
                .adDeliveryStatus(false)
                .proposal(proposal)
                .build();
        contractRepository.save(contract);

        // when
        int updated = contractRepository.updateAdDeliveryStatusToCompleted("cont23");
        assertEquals(1, updated);

        // then
        em.flush();
        em.clear();
        Contract updatedContract = contractRepository.findById("cont23").orElseThrow();
        assertEquals(true, updatedContract.getAdDeliveryStatus()); // 실제 상태 변경 확인// 상태가 1로 변경

    }

}
