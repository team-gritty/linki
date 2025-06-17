package com.Gritty.Linki.domain.user.advertiser.campaign.service;

import com.Gritty.Linki.domain.user.advertiser.campaign.dto.CampaignDto;
import com.Gritty.Linki.domain.user.advertiser.campaign.repository.CampaignRepository;
import com.Gritty.Linki.entity.Advertiser;
import com.Gritty.Linki.entity.Campaign;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 광고주 캠페인 CRUD 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CampaignService {

        private final CampaignRepository campaignRepository;
        private final EntityManager entityManager;

        /**
         * 캠페인 생성
         * @param campaignDto
         * @param advertiserId
         * @return
         */
        public CampaignDto createCampaign(CampaignDto campaignDto, String advertiserId) {
                log.info("광고주 {}의 캠페인을 생성합니다", advertiserId);

                // 엔티티 매니저로 부터 Advertiser 참조 가져오기
                Advertiser advertiser = entityManager.getReference(Advertiser.class, advertiserId);

                Campaign campaign = Campaign.builder()
                                .campaignName(campaignDto.getCampaignName())
                                .campaignDesc(campaignDto.getCampaignDesc())
                                .campaignCondition(campaignDto.getCampaignCondition())
                                .campaignImg(campaignDto.getCampaignImg())
                                .createdAt(LocalDateTime.now())
                                .campaignDeadline(campaignDto.getCampaignDeadline())
                                .campaignPublishStatus(campaignDto.getCampaignPublishStatus())
                                .campaignCategory(campaignDto.getCampaignCategory())
                                .advertiser(advertiser)
                                .build();

                Campaign savedCampaign = campaignRepository.save(campaign);
                log.info("캠페인이 성공적으로 생성되었습니다. 캠페인 ID: {}", savedCampaign.getCampaignId());

                return mapToDto(savedCampaign);
        }

        /**
         * 광고주 캠페인 수정
         * @param campaignId
         * @param campaignDto
         * @param advertiserId
         * @return
         */
        public CampaignDto updateCampaign(String campaignId, CampaignDto campaignDto, String advertiserId) {
                log.info("광고주 {}의 캠페인 {}을 수정합니다", advertiserId, campaignId);

                // 광고주 아이디와 캠페인 아이디 받아서 레포에서 찾기
                Campaign campaign = campaignRepository.findByCampaignIdAndAdvertiserId(campaignId, advertiserId)
                                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없거나 접근 권한이 없습니다"));

                campaign.setCampaignName(campaignDto.getCampaignName());
                campaign.setCampaignDesc(campaignDto.getCampaignDesc());
                campaign.setCampaignCondition(campaignDto.getCampaignCondition());
                campaign.setCampaignImg(campaignDto.getCampaignImg());
                campaign.setCampaignDeadline(campaignDto.getCampaignDeadline());
                campaign.setCampaignPublishStatus(campaignDto.getCampaignPublishStatus());
                campaign.setCampaignCategory(campaignDto.getCampaignCategory());

                Campaign updatedCampaign = campaignRepository.save(campaign);
                log.info("캠페인이 성공적으로 수정되었습니다. 캠페인 ID: {}", campaignId);

                return mapToDto(updatedCampaign);
        }

        /**
         * 광고주 캠페인 삭제
         * @param campaignId
         * @param advertiserId
         */
        public void deleteCampaign(String campaignId, String advertiserId) {
                log.info("광고주 {}의 캠페인 {}을 삭제합니다", advertiserId, campaignId);

                // 광고주 아이디와 캠페인 아이디로 삭제할 캠페인 찾기
                Campaign campaign = campaignRepository.findByCampaignIdAndAdvertiserId(campaignId, advertiserId)
                                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없거나 접근 권한이 없습니다"));

                campaignRepository.delete(campaign);
                log.info("캠페인이 성공적으로 삭제되었습니다. 캠페인 ID: {}", campaignId);
        }

        /**
         * 광고주 캠페인 공개/비공개 전환
         * @param campaignIds
         * @param makePublic
         * @param advertiserId
         * @return
         */
        public List<CampaignDto> toggleCampaignsVisibility(List<String> campaignIds, boolean makePublic,
                        String advertiserId) {
                log.info("광고주 {}의 캠페인들 {}의 공개 상태를 {}로 변경합니다", advertiserId, campaignIds, makePublic ? "공개" : "비공개");

                // 광고주아이디와 캠페인 아이디로 캠페인 여러개 찾기 (상태 변경할 캠페인 찾는 것임)
                List<Campaign> campaigns = campaignRepository.findByCampaignIdsAndAdvertiserId(campaignIds,
                                advertiserId);

                if (campaigns.size() != campaignIds.size()) {
                        throw new RuntimeException("일부 캠페인을 찾을 수 없거나 접근 권한이 없습니다");
                }

                campaigns.forEach(campaign -> campaign.setCampaignPublishStatus(
                                makePublic ? com.Gritty.Linki.vo.enums.CampaignPublishStatus.ACTIVE
                                                : com.Gritty.Linki.vo.enums.CampaignPublishStatus.HIDDEN));

                List<Campaign> updatedCampaigns = campaignRepository.saveAll(campaigns);
                log.info("{}개의 캠페인 공개 상태가 성공적으로 변경되었습니다", updatedCampaigns.size());

                return updatedCampaigns.stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());
        }

        /**
         * 광고주의 모든 캠페인 조회
         * @param advertiserId
         * @return
         */
        @Transactional(readOnly = true)
        public List<CampaignDto> getCampaignsByAdvertiserId(String advertiserId) {
                log.info("광고주 {}의 모든 캠페인을 조회합니다", advertiserId);

                List<Campaign> campaigns = campaignRepository.findByAdvertiserId(advertiserId);
                log.info("광고주 {}의 캠페인 {}개를 조회했습니다", advertiserId, campaigns.size());

                return campaigns.stream()
                                .map(this::mapToDto)
                                .collect(Collectors.toList());
        }

        /**
         * 특정 캠페인 상세 조회
         * @param campaignId
         * @param advertiserId
         * @return
         */
        @Transactional(readOnly = true)
        public CampaignDto getCampaignById(String campaignId, String advertiserId) {
                log.info("광고주 {}의 캠페인 {}을 조회합니다", advertiserId, campaignId);

                Campaign campaign = campaignRepository.findByCampaignIdAndAdvertiserId(campaignId, advertiserId)
                                .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없거나 접근 권한이 없습니다"));

                log.info("캠페인 조회가 완료되었습니다. 캠페인 ID: {}", campaignId);
                return mapToDto(campaign);
        }

        /**
         * Campaign 엔티티를 DTO로 변환
         * @param campaign
         * @return
         */
        private CampaignDto mapToDto(Campaign campaign) {
                return CampaignDto.builder()
                                .campaignId(campaign.getCampaignId())
                                .campaignName(campaign.getCampaignName())
                                .campaignDesc(campaign.getCampaignDesc())
                                .campaignCondition(campaign.getCampaignCondition())
                                .campaignImg(campaign.getCampaignImg())
                                .createdAt(campaign.getCreatedAt())
                                .campaignDeadline(campaign.getCampaignDeadline())
                                .campaignPublishStatus(campaign.getCampaignPublishStatus())
                                .campaignCategory(campaign.getCampaignCategory())
                                .advertiserId(campaign.getAdvertiser().getAdvertiserId())
                                .build();
        }
}