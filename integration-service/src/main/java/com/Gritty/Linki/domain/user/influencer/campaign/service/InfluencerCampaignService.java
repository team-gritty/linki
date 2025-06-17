package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.vo.enums.Category;

import java.util.List;

public interface InfluencerCampaignService {
    // 캠페인 전체 조회
    List<CampaignListResponseDTO> getAllCampaigns();

    // 특정 캠페인 상세 조회
    CampaignDetailResponseDTO getCampaignDetailById(String campaignId);

    //카테고리별 캠페인 리스트 조회
    List<CampaignListResponseDTO>getCampaignsByCategory(Category category);
}
