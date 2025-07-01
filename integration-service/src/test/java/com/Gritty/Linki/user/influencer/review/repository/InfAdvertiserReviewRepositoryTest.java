package com.Gritty.Linki.user.influencer.review.repository;

import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.InfAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.WrittenAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.review.repository.jpa.InfAdvertiserReviewRepository;
import com.Gritty.Linki.entity.AdvertiserReview;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.vo.enums.ContractStatus;
import com.Gritty.Linki.vo.enums.SettlementStatus;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
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

    @Test
    @DisplayName("캠페인 ID로 광고주 리뷰 조회 테스트")
    void findReviewsByCampaignIdTest() {
        // given
        String campaignId = "CAMP0000"; // 이 캠페인이 proposal - contract - review로 연결되어 있어야 함

        // when
        List<InfAdvertiserReviewResponseDTO> reviews = infAdvertiserReviewRepository.findReviewsByCampaignId(campaignId);

        // then
        assertThat(reviews).isNotEmpty();
        reviews.forEach(review -> {
            log.info("리뷰 내용: " + review.getAdvertiserReviewComment());
            assertThat(review.getContractId()).isNotBlank();
            assertThat(review.getAdvertiserReviewScore()).isNotNull();
            assertThat(review.getAdvertiserReviewCreatedAt()).isNotNull();
        });
    }

    @Test
    @DisplayName("인플루언서가 작성한 광고주 리뷰 조회")
    void findWrittenAdvertiserReviewsByInfluencerIdTest() {
        // given
        String influencerId = "INF0000"; // 실제 존재하는 influencerId로 교체

        // when
        List<WrittenAdvertiserReviewResponseDTO> reviews =
                infAdvertiserReviewRepository.findWrittenAdvertiserReviewsByInfluencerId(influencerId);

        // then
        assertThat(reviews).isNotNull();
        assertThat(reviews).allSatisfy(review -> {
            assertThat(review.getAdvertiserReviewId()).isNotBlank();
            assertThat(review.getAdvertiserReviewScore()).isBetween(BigDecimal.ZERO, BigDecimal.valueOf(5.0));
            assertThat(review.getAdvertiserReviewCreatedAt()).isNotNull();
            assertThat(review.getContractId()).isNotBlank();
            assertThat(review.getCampaignName()).isNotBlank();
            assertThat(review.getVisibility()).isNotNull();
        });

        reviews.forEach(review ->
                log.info("✅ 광고주 리뷰 ID: {}, 점수: {}, 생성일: {}, 공개여부: {}, 계약 ID: {}, 캠페인명: {}",
                        review.getAdvertiserReviewId(),
                        review.getAdvertiserReviewScore(),
                        review.getAdvertiserReviewCreatedAt(),
                        review.getVisibility(),
                        review.getContractId(),
                        review.getCampaignName()
                )
        );
    }




}
