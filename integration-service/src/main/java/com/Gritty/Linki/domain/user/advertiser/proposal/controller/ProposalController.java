package com.Gritty.Linki.domain.user.advertiser.proposal.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.proposal.dto.ProposalDto;
import com.Gritty.Linki.domain.user.advertiser.proposal.request.ProposalAcceptRequest;
import com.Gritty.Linki.domain.user.advertiser.proposal.request.ProposalRejectRequest;
import com.Gritty.Linki.domain.user.advertiser.proposal.request.ProposalUpdateRequest;
import com.Gritty.Linki.domain.user.advertiser.proposal.response.ProposalResponse;
import com.Gritty.Linki.domain.user.advertiser.proposal.service.ProposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 광고주 제안서 관리 컨트롤러
 */
@RestController
@RequestMapping("/v1/api/advertiser")
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;

    /**
     * 캠페인별 제안서 목록 조회 (캠페인 상세 페이지용)
     * 
     * @param campaignId 캠페인 ID (쿼리 파라미터)
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return 제안서 목록
     */
    @GetMapping("/proposals")
    public ResponseEntity<List<ProposalResponse>> getProposalsByCampaignQuery(
            @RequestParam String campaignId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("캠페인 {} 제안서 목록 조회 요청 (쿼리 파라미터), 사용자: {}", campaignId, user.getUserId());

        List<ProposalDto> proposals = proposalService.getProposalsByCampaign(campaignId, user);

        List<ProposalResponse> responses = proposals.stream()
                .map(this::dtoToResponse)
                .collect(Collectors.toList());

        log.info("캠페인 {} 제안서 {}개 조회 완료", campaignId, responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * 제안서 상세 조회 (캠페인 상세 페이지용)
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID (쿼리 파라미터)
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return 제안서 상세 정보
     */
    @GetMapping("/proposals/{proposalId}")
    public ResponseEntity<ProposalResponse> getProposalDetail(
            @PathVariable String proposalId,
            @RequestParam String campaignId,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("제안서 {} 상세 조회 요청, 캠페인: {}, 사용자: {}", proposalId, campaignId, user.getUserId());

        ProposalDto proposal = proposalService.getProposalDetail(proposalId, campaignId, user);
        ProposalResponse response = dtoToResponse(proposal);

        log.info("제안서 {} 상세 조회 완료", proposalId);
        return ResponseEntity.ok(response);
    }

    /**
     * 제안서 수락 (캠페인 상세 페이지용)
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID (쿼리 파라미터)
     * @param request    수락 요청 데이터
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return 수락된 제안서 정보
     */
    @PostMapping("/proposals/{proposalId}/accept")
    public ResponseEntity<ProposalResponse> acceptProposal(
            @PathVariable String proposalId,
            @RequestParam String campaignId,
            @RequestBody(required = false) ProposalAcceptRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("제안서 {} 수락 요청, 캠페인: {}, 사용자: {}", proposalId, campaignId, user.getUserId());

        ProposalDto acceptedProposal = proposalService.acceptProposal(proposalId, campaignId, user);
        ProposalResponse response = dtoToResponse(acceptedProposal);

        log.info("제안서 {} 수락 완료", proposalId);
        return ResponseEntity.ok(response);
    }

    /**
     * 제안서 거절 (캠페인 상세 페이지용)
     * 
     * @param proposalId 제안서 ID
     * @param campaignId 캠페인 ID (쿼리 파라미터)
     * @param request    거절 요청 데이터
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return 거절된 제안서 정보
     */
    @PostMapping("/proposals/{proposalId}/reject")
    public ResponseEntity<ProposalResponse> rejectProposal(
            @PathVariable String proposalId,
            @RequestParam String campaignId,
            @RequestBody(required = false) ProposalRejectRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("제안서 {} 거절 요청, 캠페인: {}, 사용자: {}", proposalId, campaignId, user.getUserId());

        ProposalDto rejectedProposal = proposalService.rejectProposal(proposalId, campaignId, user);
        ProposalResponse response = dtoToResponse(rejectedProposal);

        log.info("제안서 {} 거절 완료", proposalId);
        return ResponseEntity.ok(response);
    }

    /**
     * 제안서 수정
     * 
     * @param campaignId 캠페인 ID
     * @param proposalId 제안서 ID
     * @param request    수정 요청 데이터
     * @param user       JWT 토큰에서 추출된 사용자 정보
     * @return 수정 완료 응답
     */
    @PutMapping("/campaigns/{campaignId}/proposals/{proposalId}")
    public ResponseEntity<Void> updateProposal(
            @PathVariable String campaignId,
            @PathVariable String proposalId,
            @Valid @RequestBody ProposalUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {

        log.info("제안서 {} 수정 요청, 사용자: {}", proposalId, user.getUserId());

        proposalService.updateProposal(proposalId, user, request.getContents());

        log.info("제안서 {} 수정 완료", proposalId);
        return ResponseEntity.ok().build();
    }

    /**
     * DTO를 Response로 변환하는 헬퍼 메소드
     * 
     * @param dto 제안서 DTO
     * @return 제안서 Response
     */
    private ProposalResponse dtoToResponse(ProposalDto dto) {
        return ProposalResponse.builder()
                .proposalId(dto.getProposalId())
                .contents(dto.getContents())
                .status(dto.getStatus())
                .submittedAt(dto.getSubmittedAt())
                .respondedAt(dto.getRespondedAt())
                .influencerId(dto.getInfluencerId())
                .campaignId(dto.getCampaignId())
                .campaignName(dto.getCampaignName())
                .influencerName(dto.getInfluencerName())
                .build();
    }
}