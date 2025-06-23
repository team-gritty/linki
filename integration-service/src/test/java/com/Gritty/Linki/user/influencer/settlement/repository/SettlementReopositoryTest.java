package com.Gritty.Linki.user.influencer.settlement.repository;

import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;
import com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa.InfSettlementRepository;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.vo.enums.*;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
@EnableJpaRepositories(basePackageClasses = InfSettlementRepository.class)
@DisplayName("SettlementRepository 테스트")
public class SettlementReopositoryTest {

    @Autowired
    InfSettlementRepository infSettlementRepository;

    @Autowired
    private TestEntityManager em;


    @Test
    @DisplayName("정산 상태가 COMPLETED이고 계약 상태도 COMPLETED인 경우만 조회된다")
    void findAllByInfluencerIdAndCompletedContract_success() {
        // given
        User user = User.builder()
                .userId("USER1234")
                .userLoginId("user0")
                .userLoginPw("123456")
                .userName("사용자0")
                .userPhone("010-7113-9755")
                .userEmail("user0@example.com")
                .userPayStatus(1)
                .userStatus(1)
                .userEnterDay(LocalDate.now())
                .userRole("ROLE_INFLUENCER")
                .build();
        Influencer influencer = new Influencer("INF1234","USER0000",user);
        em.persist(influencer);

        Advertiser advertiser = Advertiser.builder()
                        .advertiserId("ADV1234")
                        .userId("USER0501") // userId는 임의값
                        .businessNumber("123-45-0001")
                        .companyName("회사1")
                        .build();
        em.persist(advertiser);


        Campaign campaign = Campaign.builder()
                        .campaignId("CAMP1234")
                        .campaignName("캠페인0")
                        .campaignCategory(Category.EDUCATION)
                        .campaignCondition("캠페인 조건0")
                        .campaignDesc("캠페인 설명0")
                        .campaignImg("https://images.unsplash.com/photo-1505740420928-5e560c0")
                        .campaignDeadline(LocalDateTime.of(2025, 3, 8, 0, 0, 0))
                        .campaignPublishStatus(CampaignPublishStatus.ACTIVE)
                        .advertiser(advertiser)
                        .createdAt(LocalDateTime.now())
                        .build();

        em.persist(campaign);

        Proposal proposal =Proposal.builder()
                        .submittedAt(LocalDateTime.of(2024, 3, 8, 0, 0, 0))
                        .proposalId("PROP1234")
                        .contents("제안내용0")
                        .respondedAt(LocalDateTime.now())
                        .influencer(influencer)
                        .campaign(campaign)
                        .status(ProposalStatus.ACCEPTED)
                        .build();
        em.persist(proposal);

        Contract contract = Contract.builder()
                .contractCompletedAt(LocalDateTime.now())
                .contractAmount(BigDecimal.valueOf(1000000))
                .contractCreatedAt(LocalDateTime.now())
                .contractId("CONT1234")
                .contractEndDate(LocalDate.now())
                .contractStartDate(LocalDate.now())
                .contractStatus(ContractStatus.COMPLETED)
                .contractTitle("계약서0")
                .contractPaymentDate(LocalDate.now())
                .adDeliveryStatus(true)
                .contractSpecialTerms("특약")
                .eventType("api")
                .documentName("문서명")
                .documentId("doc123")
                .pdfFilePath("파일경로")
                .proposal(proposal)
                .build();
        em.persist(contract);


        Settlement settlement = Settlement.builder()
                        .influencer(influencer).settlementId("SET1234").settlementAmount(BigDecimal.valueOf(1000000)).settlementDate(LocalDate.now()).settlementStatus(SettlementStatus.COMPLETED)
                        .contract(contract).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                        em.persist(settlement);

        em.flush();
        em.clear();

        // when
        List<SettlementResponseDTO> result = infSettlementRepository
                .findAllByInfluencerIdAndCompletedContract("INF1234");

        // then
        assertThat(result).hasSize(1);
        SettlementResponseDTO dto = result.get(0);
        assertThat(dto.getSettlementId()).isEqualTo("SET1234");
        assertThat(dto.getContractId()).isEqualTo("CONT1234");
        assertThat(dto.getSettlementAmount().compareTo(new BigDecimal("1000000"))).isZero();
        assertThat(SettlementStatus.valueOf(String.valueOf(dto.getSettlementStatus()))).isEqualTo(SettlementStatus.COMPLETED);
        assertThat(dto.getCampaignName()).isEqualTo("캠페인0");
    }



}
