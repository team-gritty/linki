package com.Gritty.Linki.domain.user.influencer.proposal.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.proposal.service.InfluencerProposalService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
}
