package com.Gritty.Linki.domain.user.influencer.proposal.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.proposal.service.InfluencerProposalService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalListResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfluencerProposalController {

    private final InfluencerProposalService influencerProposalService;

    @PostMapping("/v1/api/influencer/mypage/campaigns/{campaignId}/proposals")
    @PreAuthorize("hasRole('ROLE_INFLUENCER')")
    public ResponseEntity<ProposalResponseDTO>submitProposal(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String campaignId,
            @RequestBody @Valid ProposalRequestDTO proposalRequestDTO
    ){
        proposalRequestDTO.setCampaignId(campaignId);
        ProposalResponseDTO responseDTO = influencerProposalService.submitProposal(customUserDetails, proposalRequestDTO);
        return ResponseEntity.ok(responseDTO);

    }

    @GetMapping("/v1/api/influencer/mypage/proposals")
    public ResponseEntity<List<ProposalListResponseDTO>> getMyProposals(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        List<ProposalListResponseDTO>list = influencerProposalService.getMyProposals(customUserDetails);
        return ResponseEntity.ok(list);


    }

    @PutMapping("/v1/api/influencer/mypage/proposals/{prooposalId}")
    public ResponseEntity<String>updateProposal(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String prooposalId,
            @RequestBody ProposalRequestDTO proposalRequestDTO
    ) throws AccessDeniedException {
        influencerProposalService.updateProposal(userDetails,prooposalId,proposalRequestDTO);
        return ResponseEntity.ok("ok");



    }

    @GetMapping("/v1/api/influencer/mypage/proposals/{proposalId}")
    public ResponseEntity<ProposalDetailResponseDTO> detailProposal(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String proposalId
    ){
        ProposalDetailResponseDTO response = influencerProposalService.getProposalDetail(userDetails,proposalId);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/v1/api/influencer/mypage/proposals/{proposalId}")
    public ResponseEntity<String> deleteProposal(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable String proposalId
    ) throws AccessDeniedException {

        influencerProposalService.deleteProposal(user,proposalId);
        return ResponseEntity.noContent().build(); // 204 no content
        
    }
}
