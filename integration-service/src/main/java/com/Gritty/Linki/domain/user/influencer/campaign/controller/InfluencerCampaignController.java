package com.Gritty.Linki.domain.user.influencer.campaign.controller;

import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignCategoryResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.CampaignListResponseDTO;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.vo.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerCampaignController {

    private final InfluencerCampaignService influencerCampaignService;


    // 캠페인 전체 조회
    @GetMapping("/v1/api/nonuser/campaigns")
    public ResponseEntity<List<CampaignListResponseDTO>>getAllCampaigns(){
        return ResponseEntity.ok(influencerCampaignService.getAllCampaigns());

    }

    // 캠페인 상세 조회
    @GetMapping("/v1/api/nonuser/campaigns/{campaignId}")
    public ResponseEntity<CampaignDetailResponseDTO>getcampaignDetails(@PathVariable String campaignId){
        return ResponseEntity.ok(influencerCampaignService.getCampaignDetailById(campaignId));
    }

    // 카테고리별 캠페인 리스트 조회
    @GetMapping("/v1/api/nonuser/campaigns/categories/{category}")
    public ResponseEntity<List<CampaignListResponseDTO>> getCampaignsByCategories(@PathVariable("category") Category category){
        return ResponseEntity.ok(influencerCampaignService.getCampaignsByCategory(category));

    }

    // 로그인 한 인플루언서의 4개탭 캠페인 상세 조회
    @GetMapping("/v1/api/influencer/campaigns/proposals/{proposalId}")
    public ResponseEntity<CampaignDetailResponseDTO> getInfluencerCampaignDetail(@PathVariable String proposalId, @AuthenticationPrincipal User user){
        CampaignDetailResponseDTO dto = influencerCampaignService.getCampaignByProposalId(proposalId);
//        log.info(user.userΩgetUserName());
        log.info(user);
        return ResponseEntity.ok(dto);
    }

    // 카테고리 조회
    @GetMapping("/v1/api/nonuser/categories")
    public ResponseEntity<List<CampaignCategoryResponseDTO>> getCategories(){
        List<CampaignCategoryResponseDTO> categories = influencerCampaignService.getCategories();
        return ResponseEntity.ok(categories);
    }
}
