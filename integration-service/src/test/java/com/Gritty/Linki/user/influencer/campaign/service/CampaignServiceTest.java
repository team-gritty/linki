package com.Gritty.Linki.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.vo.enums.Category;
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

    @Test
    void testGetCampaignDetailsById(){
        // given
        String campaignId = "CAMP0001";

        // when
        CampaignDetailResponseDTO dto = campaignService.getCampaignDetailById(campaignId);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getCampaignId()).isEqualTo(campaignId);
        assertThat(dto.getCampaignName()).isNotNull();

        System.out.println("✅ 캠페인 이름: " + dto.getCampaignName());
        System.out.println("✅ 광고주 회사명: " + dto.getCompanyName());


    }
    @Test
    void testGetcampaignsByCategory(){
        // when
        List<CampaignListResponseDTO> beauties = campaignService.getCampaignsByCategory(Category.BEAUTY);

        // then
        assertThat(beauties)
                .isNotNull()
                .isNotEmpty()
                .allSatisfy(dto -> assertThat(dto.getCampaignCategory()).isEqualTo(Category.BEAUTY));

        // 출력
        beauties.forEach(dto ->
                System.out.println(
                        "🎨 캠페인ID=" + dto.getCampaignId() +
                                ", 제목=" + dto.getCampaignName() +
                                ", 카테고리=" + dto.getCampaignCategory()
                )
        );


    }
}
