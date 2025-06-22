package com.Gritty.Linki.domain.user.influencer.review.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.ReceivedInfluencerReviewResponseDTO;
import com.Gritty.Linki.entity.InfluencerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InfInfluencerReviewRepository extends JpaRepository<InfluencerReview,String> {

    // 로그인한 인플루언서가 광고주에게 받은 리뷰 조회
    @Query("""
    SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.ReceivedInfluencerReviewResponseDTO(
        r.influencerReviewId,
        r.influencerReviewScore,
        r.influencerReviewComment,
        r.influencerReviewCreatedAt,
        c.contractId,
        r.visibility,
        camp.campaignName
    )
    FROM InfluencerReview r
    JOIN r.contract c
    JOIN Settlement s ON s.contract.contractId = c.contractId
    JOIN Proposal p ON c.proposal.proposalId = p.proposalId
    JOIN Campaign camp ON p.campaign.campaignId = camp.campaignId
    WHERE s.influencer.influencerId = :influencerId
      AND s.settlementStatus = 'COMPLETED'
""")
    List<ReceivedInfluencerReviewResponseDTO> findReceivedInfluencerReviews(@Param("influencerId") String influencerId);

}
