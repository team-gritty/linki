package com.Gritty.Linki.domain.user.influencer.campaign.controller;

import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerCampaignController {

    private final InfluencerCampaignService influencerCampaignService;


    @GetMapping("/v1/api/nonuser/campaigns")
    public ResponseEntity<List<CampaignListResponseDTO>>getcampigns(){
        return ResponseEntity.ok(influencerCampaignService.getAllCampaigns());

    }
}
