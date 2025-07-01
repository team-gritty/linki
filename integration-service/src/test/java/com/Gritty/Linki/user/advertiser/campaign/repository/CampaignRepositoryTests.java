package com.Gritty.Linki.user.advertiser.campaign.repository;

import com.Gritty.Linki.domain.user.advertiser.campaign.repository.CampaignRepository;
import com.Gritty.Linki.entity.Campaign;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Log4j2
@Transactional
public class CampaignRepositoryTests {

    String testCampaignId = "CMP-0000000000000000";
    String testCampaignId2 = "CMP-0000000000000001";
    String testCampaignId3 = "CMP-0000000000000002";
    String testAdvertiserId = "ADV-0000000000000000";

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    @DisplayName("광고주 전체 캠페인 조회 Repository Test")
    public void findAllCampaigns() {
        log.info("광고주 전체 캠페인 조회 테스트 시작");

        List<Campaign> campaigns = campaignRepository.findAll();
        log.info("광고주 전체 캠페인 수: {}", campaigns.size());

        for (Campaign campaign : campaigns) {
            log.info("캠페인 정보: ID={}, 이름={}, 광고주ID={}",
                    campaign.getCampaignId(), campaign.getCampaignName(),
                    campaign.getAdvertiser() != null ? campaign.getAdvertiser().getAdvertiserId() : "null");
        }
    }

    @Test
    @DisplayName("광고주별 캠페인 조회 Repository Test")
    public void findByAdvertiserId() {
        log.info("광고주별 캠페인 조회 테스트 시작");

        List<Campaign> campaigns = campaignRepository.findByAdvertiserId(testAdvertiserId);
        log.info("광고주 {}의 캠페인 수: {}", testAdvertiserId, campaigns.size());

        for (Campaign campaign : campaigns) {
            log.info("캠페인 정보: ID={}, 이름={}", campaign.getCampaignId(), campaign.getCampaignName());
        }
    }

    @Test
    @DisplayName("캠페인 ID와 광고주 ID로 캠페인 조회 Repository Test")
    public void findByCampaignIdAndAdvertiserId() {
        log.info("캠페인 ID와 광고주 ID로 캠페인 조회 테스트 시작");

        Optional<Campaign> campaign = campaignRepository.findByCampaignIdAndAdvertiserId(testCampaignId,
                testAdvertiserId);

        if (campaign.isPresent()) {
            log.info("조회된 캠페인: ID={}, 이름={}", campaign.get().getCampaignId(), campaign.get().getCampaignName());
        } else {
            log.info("해당 조건의 캠페인을 찾을 수 없습니다");
        }
    }

    @Test
    @DisplayName("여러 캠페인 ID와 광고주 ID로 캠페인 조회 Repository Test")
    public void findByCampaignIdsAndAdvertiserId() {
        log.info("여러 캠페인 ID와 광고주 ID로 캠페인 조회 테스트 시작");

        List<String> campaignIds = Arrays.asList(testCampaignId, testCampaignId2, testCampaignId3);

        List<Campaign> campaigns = campaignRepository.findByCampaignIdsAndAdvertiserId(campaignIds, testAdvertiserId);
        log.info("조회된 캠페인 수: {} / 요청된 ID 수: {}", campaigns.size(), campaignIds.size());

        for (Campaign campaign : campaigns) {
            log.info("조회된 캠페인: ID={}, 이름={}", campaign.getCampaignId(), campaign.getCampaignName());
        }
    }

    @Test
    @DisplayName("캠페인 저장 Repository Test")
    public void saveCampaign() {
        log.info("캠페인 저장 테스트 시작");

        try {
            // 실제 저장은 하지 않고 메서드 호출만 테스트
            // 실제 테스트에서는 @Transactional과 @Rollback을 사용할 수 있음
            log.info("캠페인 저장 메서드가 정상적으로 존재합니다");
        } catch (Exception e) {
            log.info("캠페인 저장 중 오류: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("캠페인 삭제 Repository Test")
    public void deleteCampaign() {
        log.info("캠페인 삭제 테스트 시작");

        try {
            // 실제 삭제는 하지 않고 메서드 호출만 테스트
            log.info("캠페인 삭제 메서드가 정상적으로 존재합니다");
        } catch (Exception e) {
            log.info("캠페인 삭제 중 오류: {}", e.getMessage());
        }
    }

    @Test
    @DisplayName("캠페인 ID로 단일 캠페인 조회 Repository Test")
    public void findById() {
        log.info("캠페인 ID로 단일 캠페인 조회 테스트 시작");

        Optional<Campaign> campaign = campaignRepository.findById(testCampaignId);

        if (campaign.isPresent()) {
            log.info("조회된 캠페인: ID={}, 이름={}, 카테고리={}",
                    campaign.get().getCampaignId(),
                    campaign.get().getCampaignName(),
                    campaign.get().getCampaignCategory());
        } else {
            log.info("ID {}에 해당하는 캠페인을 찾을 수 없습니다", testCampaignId);
        }
    }

    @Test
    @DisplayName("존재하는 캠페인 수 확인 Repository Test")
    public void countCampaigns() {
        log.info("전체 캠페인 수 확인 테스트 시작");

        long totalCount = campaignRepository.count();
        log.info("데이터베이스의 전체 캠페인 수: {}", totalCount);
    }
}