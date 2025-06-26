package com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.home.EndingTodayCampaignResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.vo.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 카테고리별 조회 (페이지네이션)
    Page<Campaign> findAllByCampaignCategory(Category category, Pageable pageable);

    // 전체 캠페인 조회 (페이지네이션)
    Page<Campaign> findAll(Pageable pageable);

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

    // 마감 임박 캠페인 조회

    @Query("""
SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.home.EndingTodayCampaignResponseDTO(
    c.campaignId,
    c.campaignName,
    c.campaignCondition,
    c.campaignImg,
    c.createdAt,
    c.campaignDeadline,
    c.campaignPublishStatus,
    c.campaignCategory
)
FROM Campaign c
WHERE c.campaignDeadline BETWEEN :startOfDay AND :endOfDay
ORDER BY c.campaignDeadline ASC
""")
    List<EndingTodayCampaignResponseDTO> findEndingTodayCampaigns(
        @Param("startOfDay") java.time.LocalDateTime startOfDay,
        @Param("endOfDay") java.time.LocalDateTime endOfDay,
        Pageable pageable
    );
}
