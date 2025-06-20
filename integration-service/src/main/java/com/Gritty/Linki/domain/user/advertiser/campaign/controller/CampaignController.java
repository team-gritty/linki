package com.Gritty.Linki.domain.user.advertiser.campaign.controller;

import com.Gritty.Linki.domain.user.advertiser.campaign.dto.CampaignDto;
import com.Gritty.Linki.domain.user.advertiser.campaign.request.CampaignRequest;
import com.Gritty.Linki.domain.user.advertiser.campaign.response.CampaignResponse;
import com.Gritty.Linki.domain.user.advertiser.campaign.service.CampaignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/advertiser/mypage/campaigns")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {

    private final CampaignService campaignService;

    /**
     * 광고주 마이페이지 모든 캠페인 조회
     *
     * @param req
     * @return
     */
    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getCampaigns(HttpServletRequest req) {
        log.info("광고주 마이페이지 모든 캠페인 조회--------------------");

        // advertiserId는 인증에서 가져와야 하지만 여기서는 임시로 설정
        String advertiserId = getAdvertiserIdFromRequest(req);

        List<CampaignDto> campaigns = campaignService.getCampaignsByAdvertiserId(advertiserId);

        List<CampaignResponse> responses = campaigns.stream()
                .map(this::dtoToResponse)
                .collect(Collectors.toList());

        log.info("캠페인 {}개 조회 완료", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * 광고주의 특정 캠페인 조회
     *
     * @param id
     * @param req
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaign(
            @PathVariable String id,
            HttpServletRequest req) {

        log.info("캠페인 {} 조회를 요청받았습니다", id);

        // advertiserId는 인증에서 가져와야 하지만 여기서는 임시로 설정
        String advertiserId = getAdvertiserIdFromRequest(req);

        CampaignDto campaign = campaignService.getCampaignById(id, advertiserId);
        CampaignResponse response = dtoToResponse(campaign);

        return ResponseEntity.ok(response);
    }

    /**
     * 광고주 새로운 캠페인 등록
     *
     * @param id
     * @param req
     * @param request
     * @return
     */
    @PostMapping("/{id}")
    public ResponseEntity<CampaignResponse> createCampaign(
            @PathVariable String id,
            HttpServletRequest req,
            @Valid @RequestBody CampaignRequest request) {

        log.info("광고주 {}의 캠페인 등록을 요청받았습니다", id);

        // Request를 DTO로 변환
        CampaignDto campaignDto = requestToDto(request);

        // 서비스 호출
        CampaignDto createdCampaign = campaignService.createCampaign(campaignDto, id);

        // DTO를 Response로 변환
        CampaignResponse response = dtoToResponse(createdCampaign);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 광고주 캠페인 내용 수정
     *
     * @param id
     * @param req
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable String id,
            HttpServletRequest req,
            @Valid @RequestBody CampaignRequest request) {

        log.info("캠페인 {} 수정을 요청받았습니다", id);

        // Request를 DTO로 변환
        CampaignDto campaignDto = requestToDto(request);

        // 서비스 호출 (advertiserId는 인증에서 가져와야 하지만 여기서는 임시로 설정)
        String advertiserId = getAdvertiserIdFromRequest(req);
        CampaignDto updatedCampaign = campaignService.updateCampaign(id, campaignDto, advertiserId);

        // DTO를 Response로 변환
        CampaignResponse response = dtoToResponse(updatedCampaign);

        return ResponseEntity.ok(response);
    }

    /**
     * 광고주의 캠페인 삭제
     *
     * @param id
     * @param req
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(
            @PathVariable String id,
            HttpServletRequest req) {

        log.info("캠페인 {} 삭제를 요청받았습니다", id);

        // advertiserId는 인증에서 가져와야 하지만 여기서는 임시로 설정
        String advertiserId = getAdvertiserIdFromRequest(req);

        campaignService.deleteCampaign(id, advertiserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 광고주의 캠페인을 공개/비공개 전환하기
     *
     * @param req
     * @param makePublic
     * @param campaignIds
     * @return
     */
    @PatchMapping("/visibility")
    public ResponseEntity<List<CampaignResponse>> toggleCampaignsVisibility(
            HttpServletRequest req,
            @RequestParam boolean makePublic,
            @RequestBody List<String> campaignIds) {

        log.info("캠페인들 {}의 공개 상태를 {}로 변경 요청", campaignIds, makePublic ? "공개" : "비공개");

        // advertiserId는 인증에서 가져와야 하지만 여기서는 임시로 설정
        String advertiserId = getAdvertiserIdFromRequest(req);

        List<CampaignDto> updatedCampaigns = campaignService.toggleCampaignsVisibility(campaignIds, makePublic,
                advertiserId);

        List<CampaignResponse> responses = updatedCampaigns.stream()
                .map(this::dtoToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }



    // Request를 DTO로 변환하는 헬퍼 메소드
    private CampaignDto requestToDto(CampaignRequest request) {
        return CampaignDto.builder()
                .campaignName(request.getCampaignName())
                .campaignDesc(request.getCampaignDesc())
                .campaignCondition(request.getCampaignCondition())
                .campaignImg(request.getCampaignImg())
                .campaignDeadline(request.getCampaignDeadline())
                .campaignPublishStatus(request.getCampaignPublishStatus())
                .campaignCategory(request.getCampaignCategory())
                .build();
    }

    // DTO를 Response로 변환하는 헬퍼 메소드
    private CampaignResponse dtoToResponse(CampaignDto dto) {
        return CampaignResponse.builder()
                .campaignId(dto.getCampaignId())
                .campaignName(dto.getCampaignName())
                .campaignDesc(dto.getCampaignDesc())
                .campaignCondition(dto.getCampaignCondition())
                .campaignImg(dto.getCampaignImg())
                .createdAt(dto.getCreatedAt())
                .campaignDeadline(dto.getCampaignDeadline())
                .campaignPublishStatus(dto.getCampaignPublishStatus())
                .campaignCategory(dto.getCampaignCategory())
                .advertiserId(dto.getAdvertiserId())
                .build();
    }

    /**
     * HttpServletRequest에서 advertiserId를 가져오는 메소드 (인증 로직에 따라 구현 필요)
     *
     * @param request
     * @return
     */
    private String getAdvertiserIdFromRequest(HttpServletRequest request) {
        // TODO: 실제 인증/인가 로직에 따라 구현 필요
        // 예: JWT 토큰에서 사용자 정보 추출, 정섭님이 준 메서드 참고하여 advertiserId 꺼내오는 유틸 메서드/클래스 작성하기
       log.info("토큰에 광고주 들어있는가 확인"+request.getHeader("Authorization"));
        return request.getHeader("X-Advertiser-Id"); // 임시 구현
    }
}