package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;

import java.util.List;

public interface InfluencerCampaignService {
    List<CampaignListResponseDTO> getAllCampaigns();

    CampaignDetailResponseDTO getCampaignDetailById(String campaignId);
}
