package com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalListResponseDTO;
import com.Gritty.Linki.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InfluencerProposalRepository extends JpaRepository<Proposal, String> {

    @Query("""
    SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalListResponseDTO(
        p.proposalId,
        p.campaign.campaignName,
        p.campaign.campaignId,
        p.submittedAt,
        p.status,
        p.influencer.influencerId
    )
    FROM Proposal p
    WHERE p.influencer.influencerId = :influencerId
""")
    List<ProposalListResponseDTO>findAllByInfluencerId(@Param("influencerId") String influencerId);

    @Query("""
    SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.proposal.ProposalDetailResponseDTO(
        p.campaign.campaignName,
        p.proposalId,
        p.contents,
        p.status,
        p.submittedAt,
        p.respondedAt,
        p.influencer.influencerId,
        p.campaign.campaignId,
        c.contractStatus
    )
    FROM Proposal p
    LEFT JOIN Contract c ON c.proposal.proposalId = p.proposalId
    WHERE p.proposalId = :proposalId
""")
    Optional<ProposalDetailResponseDTO> findDetailByProposalId(@Param("proposalId") String proposalId);
    boolean existsByCampaign_CampaignIdAndInfluencer_InfluencerId(String campaignId, String influencerId);
}
