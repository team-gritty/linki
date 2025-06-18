package com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalListResponseDTO;
import com.Gritty.Linki.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InfluencerProposalRepository extends JpaRepository<Proposal, String> {

    @Query("""
    SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalListResponseDTO(
        p.proposalId,
        p.campaign.campaignId,
        p.campaign.campaignName,
        p.submittedAt,
        p.status,
        p.influencer.influencerId
    )
    FROM Proposal p
    WHERE p.influencer.influencerId = :influencerId
""")
    List<ProposalListResponseDTO>findAllByInfluencerId(@Param("influencerId") String influencerId);
}
