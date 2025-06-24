package com.Gritty.Linki.domain.user.advertiser.campaign.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.campaign.dto.CampaignDto;
import com.Gritty.Linki.domain.user.advertiser.campaign.request.CampaignRequest;
import com.Gritty.Linki.domain.user.advertiser.campaign.response.CampaignResponse;
import com.Gritty.Linki.domain.user.advertiser.campaign.service.CampaignService;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.util.ObjectStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/api/advertiser/mypage/campaigns")
@RequiredArgsConstructor
@Slf4j
public class CampaignController {

    private final CampaignService campaignService;
    private final AuthenticationUtil authenticationUtil;
    private final ObjectStorage objectStorage;
    private final ObjectMapper objectMapper;

    /**
     * 광고주 마이페이지 모든 캠페인 조회
     *
     * @param user JWT 토큰에서 추출된 사용자 정보
     * @return
     */
    @GetMapping
    public ResponseEntity<List<CampaignResponse>> getCampaigns(
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("광고주 마이페이지 모든 캠페인 조회, 사용자: {}", user.getUserId());

        // JWT 토큰에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);

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
     * @param campaignId
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return
     */
    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignResponse> getCampaign(
            @PathVariable String campaignId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("캠페인 {} 조회 요청, 사용자: {}", campaignId, user.getUserId());

        // JWT 토큰에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);

        CampaignDto campaign = campaignService.getCampaignById(campaignId, advertiserId);
        CampaignResponse response = dtoToResponse(campaign);

        return ResponseEntity.ok(response);
    }

    /**
     * 광고주 새로운 캠페인 등록 (이미지 파일 포함)
     *
     * @param user          JWT 토큰에서 추출된 사용자 정보
     * @param campaignImage 캠페인 이미지 파일
     * @param campaignData  캠페인 등록 요청 데이터 (JSON 문자열)
     * @return
     */
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<CampaignResponse> createCampaignWithImage(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(value = "campaignImage", required = false) MultipartFile campaignImage,
            @RequestParam("campaignData") String campaignData) {

        try {
            log.info("캠페인 등록 요청 - 사용자: {}, 이미지 파일: {}", user.getUserId(),
                    campaignImage != null ? campaignImage.getOriginalFilename() : "없음");

            // JSON 문자열을 CampaignRequest 객체로 변환
            CampaignRequest request = objectMapper.readValue(campaignData, CampaignRequest.class);

            // 이미지 URL 처리
            String imageUrl = "https://via.placeholder.com/400x300?text=No+Image";
            if (campaignImage != null && !campaignImage.isEmpty()) {
                try {
                    // ObjectStorage에 이미지 업로드
                    imageUrl = objectStorage.uploadFile(campaignImage);
                    log.info("이미지 업로드 성공: {}", imageUrl);
                } catch (Exception e) {
                    log.error("이미지 업로드 실패: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(null);
                }
            }

            // Request를 DTO로 변환하고 이미지 URL 설정
            CampaignDto campaignDto = CampaignDto.builder()
                    .campaignName(request.getCampaignName())
                    .campaignDesc(request.getCampaignDesc())
                    .campaignCondition(request.getCampaignCondition())
                    .campaignImg(imageUrl)
                    .campaignDeadline(request.getCampaignDeadline())
                    .campaignPublishStatus(request.getCampaignPublishStatus())
                    .campaignCategory(request.getCampaignCategory())
                    .build();

            // 서비스 호출 - advertiserId를 전달
            String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
            CampaignDto createdCampaign = campaignService.createCampaign(campaignDto, advertiserId);

            // DTO를 Response로 변환
            CampaignResponse response = dtoToResponse(createdCampaign);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("캠페인 등록 중 오류 발생", e);
            log.error("오류 메시지: {}", e.getMessage());
            log.error("오류 클래스: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                log.error("원인: {}", e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 기존의 JSON 기반 캠페인 등록 (Base64 이미지용)
     * 기존 코드와의 호환성을 위해 유지
     */
    @PostMapping(consumes = { "application/json" })
    public ResponseEntity<CampaignResponse> createCampaign(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CampaignRequest request) {

        try {
            // Request를 DTO로 변환
            CampaignDto campaignDto = requestToDto(request);
            log.info("변환된 캠페인 DTO: {}", campaignDto);

            // 서비스 호출 - advertiserId를 전달
            String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
            CampaignDto createdCampaign = campaignService.createCampaign(campaignDto, advertiserId);
            log.info("생성된 캠페인: {}", createdCampaign);

            // DTO를 Response로 변환
            CampaignResponse response = dtoToResponse(createdCampaign);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("캠페인 등록 중 오류 발생", e);
            log.error("오류 메시지: {}", e.getMessage());
            log.error("오류 클래스: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                log.error("원인: {}", e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 광고주 캠페인 내용 수정
     *
     * @param campaignId
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @param request
     * @return
     */
    @PutMapping("/{campaignId}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable String campaignId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CampaignRequest request) {

        log.info("캠페인 {} 수정 요청, 사용자: {}", campaignId, user.getUserId());

        // Request를 DTO로 변환
        CampaignDto campaignDto = requestToDto(request);

        // JWT 토큰에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);
        CampaignDto updatedCampaign = campaignService.updateCampaign(campaignId, campaignDto, advertiserId);

        // DTO를 Response로 변환
        CampaignResponse response = dtoToResponse(updatedCampaign);

        return ResponseEntity.ok(response);
    }

    /**
     * 광고주의 캠페인 삭제
     *
     * @param campaignId
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return
     */
    @DeleteMapping("/{campaignId}")
    public ResponseEntity<Void> deleteCampaign(
            @PathVariable String campaignId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("캠페인 {} 삭제 요청, 사용자: {}", campaignId, user.getUserId());

        // JWT 토큰에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);

        campaignService.deleteCampaign(campaignId, advertiserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 광고주의 캠페인을 공개/비공개 전환하기
     *
     * @param user        JWT 토큰에서 추출된 사용자 정보
     * @param makePublic
     * @param campaignIds
     * @return
     */
    @PatchMapping("/visibility")
    public ResponseEntity<List<CampaignResponse>> toggleCampaignsVisibility(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam boolean makePublic,
            @RequestBody List<String> campaignIds) {

        log.info("캠페인들 {}의 공개 상태를 {}로 변경 요청, 사용자: {}",
                campaignIds, makePublic ? "공개" : "비공개", user.getUserId());

        // JWT 토큰에서 광고주 ID 추출
        String advertiserId = authenticationUtil.getAdvertiserIdFromUserDetails(user);

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
}