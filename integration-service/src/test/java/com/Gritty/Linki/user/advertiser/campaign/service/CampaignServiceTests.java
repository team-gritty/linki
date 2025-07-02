package com.Gritty.Linki.user.advertiser.campaign.service;

import com.Gritty.Linki.domain.user.advertiser.campaign.dto.CampaignDto;
import com.Gritty.Linki.domain.user.advertiser.campaign.service.CampaignService;
import com.Gritty.Linki.vo.enums.CampaignPublishStatus;
import com.Gritty.Linki.vo.enums.Category;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Log4j2
@Transactional
public class CampaignServiceTests {

    @Autowired
    private CampaignService campaignService;

    String testCampaignId = "CAMP0001";
    String testCampaignId2 = "CAMP0002";
    String testAdvertiserId = "ADV0000";

    @Test
    @DisplayName("캠페인 생성 Service Test")
    public void createCampaign() {
        log.info("캠페인 생성 테스트 시작");

        CampaignDto campaignDto = CampaignDto.builder()
                .campaignName("테스트 캠페인")
                .campaignDesc("테스트용 캠페인 설명입니다.")
                .campaignCondition("테스트 조건")
                .campaignImg("test-image-url")
                .campaignDeadline(LocalDateTime.now().plusDays(30))
                .campaignPublishStatus(CampaignPublishStatus.ACTIVE)
                .campaignCategory(Category.BEAUTY)
                .build();

        try {
            CampaignDto createdCampaign = campaignService.createCampaign(campaignDto, testAdvertiserId);
            log.info("생성된 캠페인: {}", createdCampaign);
        } catch (Exception e) {
            log.info("캠페인 생성 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("광고주별 캠페인 조회 Service Test")
    public void getCampaignsByAdvertiserId() {
        log.info("광고주별 캠페인 조회 테스트 시작");

        List<CampaignDto> campaigns = campaignService.getCampaignsByAdvertiserId(testAdvertiserId);
        log.info("조회된 캠페인 수: {}", campaigns.size());

        for (CampaignDto campaign : campaigns) {
            log.info("캠페인 정보: {}", campaign);
        }
    }

    @Test
    @DisplayName("캠페인 수정 Service Test")
    public void updateCampaign() {
        log.info("캠페인 수정 테스트 시작");

        CampaignDto updateDto = CampaignDto.builder()
                .campaignName("수정된 캠페인 이름")
                .campaignDesc("수정된 캠페인 설명")
                .campaignCondition("수정된 조건")
                .campaignImg("updated-image-url")
                .campaignDeadline(LocalDateTime.now().plusDays(45))
                .campaignPublishStatus(CampaignPublishStatus.HIDDEN)
                .campaignCategory(Category.ELECTRONICS)
                .build();

        try {
            CampaignDto updatedCampaign = campaignService.updateCampaign(testCampaignId, updateDto, testAdvertiserId);
            log.info("수정된 캠페인: {}", updatedCampaign);
        } catch (Exception e) {
            log.info("캠페인 수정 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("캠페인 삭제 Service Test")
    public void deleteCampaign() {
        log.info("캠페인 삭제 테스트 시작");

        try {
            campaignService.deleteCampaign(testCampaignId, testAdvertiserId);
            log.info("캠페인 삭제 완료");
        } catch (Exception e) {
            log.info("캠페인 삭제 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("캠페인 공개/비공개 전환 Service Test")
    public void toggleCampaignsVisibility() {
        log.info("캠페인 공개/비공개 전환 테스트 시작");

        List<String> campaignIds = Arrays.asList(testCampaignId, testCampaignId2);

        // 공개로 전환 테스트
        try {
            List<CampaignDto> publicCampaigns = campaignService.toggleCampaignsVisibility(
                    campaignIds, true, testAdvertiserId);
            log.info("공개로 전환된 캠페인 수: {}", publicCampaigns.size());
        } catch (Exception e) {
            log.info("공개 전환 실패 (예상됨): {}", e.getMessage());
        }

        // 비공개로 전환 테스트
        try {
            List<CampaignDto> hiddenCampaigns = campaignService.toggleCampaignsVisibility(
                    campaignIds, false, testAdvertiserId);
            log.info("비공개로 전환된 캠페인 수: {}", hiddenCampaigns.size());
        } catch (Exception e) {
            log.info("비공개 전환 실패 (예상됨): {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("특정 캠페인 상세 조회 Service Test")
    public void getCampaignById() {
        log.info("특정 캠페인 상세 조회 테스트 시작");

        try {
            CampaignDto campaign = campaignService.getCampaignById(testCampaignId, testAdvertiserId);
            log.info("조회된 캠페인: {}", campaign);
        } catch (Exception e) {
            log.info("캠페인 조회 실패 (예상됨): {}", e.getMessage());
        }
    }
}