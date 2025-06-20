package com.Gritty.Linki.user.influencer.campaign.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignCategoryResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.vo.enums.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CampaignServiceTest {
    @Autowired
    private InfluencerCampaignService campaignService;

    @BeforeEach
    void setUpAuthentication() {
        // 로그인된 인플루언서를 시뮬레이션
        CustomUserDetails user =
                new CustomUserDetails("USER0001", "user1", "$2a$10$abcdefghijklmnopqrstuvwxyz", "인플루언서");
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    void testGetAllCampaigns() {
        // when
        List<CampaignListResponseDTO> campaigns = campaignService.getAllCampaigns();

        //then
        assertThat(campaigns).isNotNull();
        assertThat(campaigns.size()).isGreaterThan(0);
        campaigns.forEach(dto -> System.out.println("📢 캠페인 제목: " + dto.getCampaignName()));

    }

    @Test
    void testGetCampaignDetailsById() {
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
    void testGetcampaignsByCategory() {
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

    @Test
    void testGetCampaignByProposalId() {
        // given
        String proposalId = "PROP0002"; // user0001: prop0002

        // when
        CampaignDetailResponseDTO dto =
                campaignService.getCampaignByProposalId(proposalId);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getCampaignId()).isNotBlank();
        System.out.println("▶ Proposal 기반 캠페인 ID: " + dto.getCampaignId());
        System.out.println("▶ 광고주 회사명: " + dto.getCompanyName());
    }

    @Test
    void testGetCategories() {
        // when
        List<CampaignCategoryResponseDTO> categories = campaignService.getCategories();

        // then
        assertThat(categories).isNotNull();
        assertThat(categories).isNotEmpty();
        assertThat(categories.size()).isEqualTo(Category.values().length);

        // 각 항목이 enum 기반으로 잘 매핑되었는지 확인
        for (Category category : Category.values()) {
            boolean matched = categories.stream()
                    .anyMatch(dto -> dto.getName().equals(category.name())
                            && dto.getDisplayName().equals(category.getDisplayName()));
            assertThat(matched)
                    .as("카테고리 %s 가 올바르게 매핑되어야 함", category.name())
                    .isTrue();


        }

    }
}
