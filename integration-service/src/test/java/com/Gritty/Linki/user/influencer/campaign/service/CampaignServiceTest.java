package com.Gritty.Linki.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CampaignServiceTest {
    @Autowired
    private InfluencerCampaignService campaignService;

    @Test
    void testGetAllCampaigns() {
        // when
        List<CampaignListResponseDTO> campaigns = campaignService.getAllCampaigns();

        //then
        assertThat(campaigns).isNotNull();
        assertThat(campaigns.size()).isGreaterThan(0);
        campaigns.forEach(dto-> System.out.println("📢 캠페인 제목: " + dto.getCampaignName()));

    }
}
