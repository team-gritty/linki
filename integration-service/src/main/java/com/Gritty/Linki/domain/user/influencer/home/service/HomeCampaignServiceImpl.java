package com.Gritty.Linki.domain.user.influencer.home.service;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.home.EndingTodayCampaignResponseDTO;
import com.Gritty.Linki.repository.InfluencerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor

public class HomeCampaignServiceImpl implements HomeCampaignService {
    private final InfluencerCampaignRepository influencerCampaignRepository;

    @Override
    public List<EndingTodayCampaignResponseDTO> getEndingTodayCampaigns() {
        // 오늘 날짜의 시작과 끝을 설정
        LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = java.time.LocalDate.now().atTime(java.time.LocalTime.MAX);
        PageRequest pageRequest = PageRequest.of(0, 10);
        return influencerCampaignRepository.findEndingTodayCampaigns(startOfDay, endOfDay, pageRequest);
    }
}
