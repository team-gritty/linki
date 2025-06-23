package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignCategoryResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignListResponseDTO;
import com.Gritty.Linki.vo.enums.Category;

import java.util.List;

public interface InfluencerCampaignService {
    // 캠페인 전체 조회
    List<CampaignListResponseDTO> getAllCampaigns();

    // 특정 캠페인 상세 조회
    CampaignDetailResponseDTO getCampaignDetailById(String campaignId);

    //카테고리별 캠페인 리스트 조회
    List<CampaignListResponseDTO>getCampaignsByCategory(Category category);

    // 인플루언서 본인의 proposaId에 연관된 캠페인만 조회
    CampaignDetailResponseDTO getCampaignByProposalId(String proposalId);

    //카테고리 불러오기
    List<CampaignCategoryResponseDTO>getCategories();


}
