package com.Gritty.Linki.user.influencer.review.repository;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.review.repository.jpa.InfAdvertiserReviewRepository;
import com.Gritty.Linki.entity.AdvertiserReview;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("InfAdvertiserReviewRepository 테스트")
public class InfAdvertiserReviewRepositoryTest {
    @Autowired
    private InfAdvertiserReviewRepository infAdvertiserReviewRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    @DisplayName("리뷰 존재 여부 true 반환")
    void existsByContractId() {
        // given
        String contractId = "CONT0000";

        // 테스트용 리뷰 엔티티작성
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(()->new RuntimeException("테스트용 계약 없음"));

        AdvertiserReview review = AdvertiserReview.builder()
                .advertiserReviewId("REVIEW_TEST_1")
                .contract(contract)
                .advertiserReviewScore(new BigDecimal("5.0"))
                .advertiserReviewComment("중복 여부 테스트")
                .advertiserReviewCreatedAt(LocalDateTime.now())
                .visibility(true)
                .build();

        infAdvertiserReviewRepository.save(review);

        // when
        boolean exists = infAdvertiserReviewRepository.existsByContract_ContractId(contractId);

        // then
        assertTrue(exists);

    }

    @Test
    @DisplayName("리뷰 존재 여부 false 반환 (리뷰 없음)")
    void existByContractId_shouldReturnFalse_ifNoReview() {
        // given
        String nonexistentContractId = "CONTRACT_NOT_EXIST";

        // when
        boolean exists = infAdvertiserReviewRepository.existsByContract_ContractId(nonexistentContractId);

        // then
        assertFalse(exists);
    }



    @Test
    void getSettlementFinishedContractsTest() {
        // given
        String influencerId = "INF0000";

        // when
        List<ReviewableContractResponseDTO> result = contractRepository.findReviewableContractsByInfluencerId(influencerId);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).allSatisfy(dto -> {
            assertThat(dto.getContractStatus()).isEqualTo(ContractStatus.COMPLETED);
            assertThat(dto.getSettlementStatus()).isEqualTo(SettlementStatus.COMPLETED);
        });
    }




}
