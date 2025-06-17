package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.influencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

    @Override
    public CampaignDetailResponseDTO getCampaignDetailById(String campaignId) {
        // Optional에서 값 꺼내기 + 예외 처리
      Campaign campaign = campaignRepository.findByCampaignIdWithAdvertiser(campaignId)
              .orElseThrow(() -> new EntityNotFoundException("해당 캠페인을 찾을 수 없습니다."));

      // ModelMapper로 DTO 매핑
        CampaignDetailResponseDTO responseDTO = modelMapper.map(campaign, CampaignDetailResponseDTO.class);

        // advertiser -> companyName 수동세팅
        responseDTO.setCompanyName(campaign.getAdvertiser().getCompanyName());

        return responseDTO;

    }
}
