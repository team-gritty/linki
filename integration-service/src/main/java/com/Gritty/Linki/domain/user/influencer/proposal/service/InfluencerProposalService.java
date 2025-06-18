package com.Gritty.Linki.domain.user.influencer.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalResponseDTO;

public interface InfluencerProposalService {

    ProposalResponseDTO submitProposal(CustomUserDetails customUserDetails, ProposalRequestDTO proposalRequestDTO);
}
