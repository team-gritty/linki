package com.Gritty.Linki.domain.user.influencer.home.controller;

import com.Gritty.Linki.domain.user.influencer.home.service.HomeCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.home.EndingTodayCampaignResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class HomeCampaignController {

    private final HomeCampaignService homeCampaignService;

    // 오늘 마감 캠페인 5개 조회
    @GetMapping("/v1/api/home/ending-today-campaigns")
    public ResponseEntity<List<EndingTodayCampaignResponseDTO>> getEndingTodayCampaigns() {
        List<EndingTodayCampaignResponseDTO> campaigns = homeCampaignService.getEndingTodayCampaigns();
        return ResponseEntity.ok(campaigns);

    }
}
