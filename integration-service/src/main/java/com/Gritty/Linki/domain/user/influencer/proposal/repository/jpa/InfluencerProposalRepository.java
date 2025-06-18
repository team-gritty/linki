package com.Gritty.Linki.domain.user.influencer.proposal.repository.jpa;

import com.Gritty.Linki.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfluencerProposalRepository extends JpaRepository<Proposal, String> {
}
