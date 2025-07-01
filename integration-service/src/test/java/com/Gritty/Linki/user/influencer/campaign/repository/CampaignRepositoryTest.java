package com.Gritty.Linki.user.influencer.campaign.repository;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.home.EndingTodayCampaignResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.vo.enums.Category;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)// 실제 DB 사용시
@Log4j2
public class CampaignRepositoryTest {

    @Autowired
    private InfluencerCampaignRepository campaignRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private InfluencerCampaignRepository influencerCampaignRepository;

    @Test
    void getAllCampaigns() {
        // when
        List<Campaign> campaigns = campaignRepository.findAll();

        // then
        assertThat(campaigns).isNotNull();
        assertThat(campaigns.size()).isGreaterThan(0); // 최소 1개 이상
        campaigns.forEach(c -> System.out.println("📌 캠페인 제목: " + c.getCampaignName()));
    }

    @Test
    void getCampaignDetails() {
        // given
        String campaignId = "CAMP0001";

        // when
        Optional<Campaign> result = campaignRepository.findByCampaignIdWithAdvertiser(campaignId);

        //then
        assertThat(result).isPresent();

        Campaign campaign = result.get();
        System.out.println("✅ 캠페인 이름: " + campaign.getCampaignName());
        System.out.println("✅ 광고주 회사명: " + campaign.getAdvertiser().getCompanyName());

        assertThat(campaign.getAdvertiser()).isNotNull();
        assertThat(campaign.getAdvertiser().getCompanyName()).isNotEmpty();

    }

    @Test
    void testGetCampaignsByCategory() {
        // given
        Category category = Category.BEAUTY;

        // when
        List<Campaign> beauties = campaignRepository.findAllByCampaignCategory(category);

        // then
        assertThat(beauties).isNotNull()
                .isNotEmpty()
                .allMatch(c -> c.getCampaignCategory() == category);
        beauties.forEach(
                c -> System.out.println("🎨 [" + c.getCampaignCategory() + "] " + c.getCampaignName()));


    }

    @Test
    void testFindCampaignByProposalId() {
        // given
        String proposalId = "PROP0002";
        String influencerId = "INF0001";

        // when
        Optional<Campaign> opt = campaignRepository.findCampaignByProposalIdAndInfluencerId(proposalId, influencerId);
        assertThat(opt).as("proposalId=%s, influencerId=%s 로 조회", proposalId, influencerId)
                .isPresent();

        Campaign c = opt.get();

        // — 실제 로드된 데이터 출력 —
        System.out.println("=== Loaded Campaign Details ===");
        System.out.println("Campaign ID    : " + c.getCampaignId());
        System.out.println("Campaign Name  : " + c.getCampaignName());
        System.out.println("Advertiser ID  : " + c.getAdvertiser().getAdvertiserId());
        System.out.println("Advertiser Name: " + c.getAdvertiser().getCompanyName());
        System.out.println("Influencer ID  : " + c.getProposals().get(0).getInfluencer().getUserId());
        System.out.println("================================");

        // then: advertiser 즉시 로딩 확인
        String before = c.getAdvertiser().getCompanyName();
        em.flush();
        em.clear();
        // 트랜잭션 풀 후에도 LazyInitializationException 없이 접근 가능
        String after = c.getAdvertiser().getCompanyName();
        assertThat(after).isEqualTo(before);


        boolean found = c.getProposals()
                .stream()
                .anyMatch(p -> proposalId.equals(p.getProposalId())
                        && influencerId.equals(p.getInfluencer().getInfluencerId()));


    }

    @Test
    void testFindTop5Campaigns() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(java.time.LocalTime.MAX);

        PageRequest pageRequest = PageRequest.of(0, 5); // 첫 페이지, 5개 조회
        List<EndingTodayCampaignResponseDTO> result =
                influencerCampaignRepository.findEndingTodayCampaigns(startOfDay, endOfDay, pageRequest);

        assertNotNull(result);
        assertTrue(result.size() <= 5);

        for (EndingTodayCampaignResponseDTO dto : result) {
            log.info(dto.getCampaignName() + " / 마감일: " + dto.getCampaignDeadline());
            assertEquals(java.time.LocalDate.now(), dto.getCampaignDeadline().toLocalDate());
        }
    }
}

