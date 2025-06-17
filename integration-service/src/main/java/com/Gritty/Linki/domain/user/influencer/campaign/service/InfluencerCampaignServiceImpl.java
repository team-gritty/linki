package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.influencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfluencerCampaignServiceImpl implements InfluencerCampaignService {

    private final influencerCampaignRepository campaignRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<CampaignListResponseDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(campaign -> modelMapper.map(campaign, CampaignListResponseDTO.class))
                .collect(Collectors.toList());
    }
}
