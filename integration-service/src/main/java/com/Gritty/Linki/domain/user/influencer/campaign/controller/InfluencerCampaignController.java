package com.Gritty.Linki.domain.user.influencer.campaign.controller;

import com.Gritty.Linki.domain.user.influencer.campaign.service.InfluencerCampaignService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignCategoryResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignListResponseDTO;
import com.Gritty.Linki.entity.User;
import com.Gritty.Linki.vo.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 캠페인 전체 조회 (페이지네이션)
    @GetMapping("/v1/api/nonuser/campaigns/page")
    public ResponseEntity<Page<CampaignListResponseDTO>> getAllCampaignsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Pagination request - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CampaignListResponseDTO> result = influencerCampaignService.getAllCampaignsWithPagination(pageable);
        log.info("Pagination response - currentPage: {}, totalPages: {}, totalElements: {}", 
                result.getNumber(), result.getTotalPages(), result.getTotalElements());
        return ResponseEntity.ok(result);
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

    // 카테고리별 캠페인 리스트 조회 (페이지네이션)
    @GetMapping("/v1/api/nonuser/campaigns/categories/{category}/page")
    public ResponseEntity<Page<CampaignListResponseDTO>> getCampaignsByCategoriesWithPagination(
            @PathVariable("category") Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        log.info("Category pagination request - category: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", 
                category, page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CampaignListResponseDTO> result = influencerCampaignService.getCampaignsByCategoryWithPagination(category, pageable);
        log.info("Category pagination response - currentPage: {}, totalPages: {}, totalElements: {}", 
                result.getNumber(), result.getTotalPages(), result.getTotalElements());
        return ResponseEntity.ok(result);
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
