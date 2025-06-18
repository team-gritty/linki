package com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa;

import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.vo.enums.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InfluencerCampaignRepository extends JpaRepository<Campaign,String> {
    @Query("SELECT c FROM Campaign c JOIN FETCH c.advertiser a WHERE c.campaignId = :campaignId")
    Optional<Campaign>findByCampaignIdWithAdvertiser(@Param("campaignId") String campaignId);

    // 카테고리별 조회
    List<Campaign> findAllByCampaignCategory(Category category);

    /**
     * proposalId, influencerId 로 Campaign 을 조회
     *
     */
    @Query("""
    SELECT c 
    FROM Campaign c 
    JOIN FETCH c.advertiser 
    JOIN c.proposals p 
    WHERE p.proposalId = :proposalId
    AND p.influencer.influencerId = :influencerId
""")
    Optional<Campaign> findCampaignByProposalIdAndInfluencerId(@Param("proposalId") String proposalId,
                                                               @Param("influencerId") String influencerId);
}
