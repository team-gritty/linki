package com.Gritty.Linki.user.influencer.campaign.repository;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.influencerCampaignRepository;
import com.Gritty.Linki.entity.Campaign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)// 실제 DB 사용시
public class CampaignRepositoryTest {

    @Autowired
    private influencerCampaignRepository campaignRepository;

    @Test
    void getAllCampaigns() {
        // when
        List<Campaign> campaigns = campaignRepository.findAll();

        // then
        assertThat(campaigns).isNotNull();
        assertThat(campaigns.size()).isGreaterThan(0); // 최소 1개 이상
        campaigns.forEach(c -> System.out.println("📌 캠페인 제목: " + c.getCampaignName()));
    }
}
