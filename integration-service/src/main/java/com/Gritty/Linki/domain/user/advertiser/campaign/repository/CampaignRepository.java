package com.Gritty.Linki.domain.user.advertiser.campaign.repository;

import com.Gritty.Linki.entity.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 광고주 캠페인 JPA 레포지토리
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {

    /**
     *
     * @param advertiserId
     * @return
     */
    @Query("SELECT c FROM Campaign c WHERE c.advertiser.advertiserId = :advertiserId")
    List<Campaign> findByAdvertiserId(@Param("advertiserId") String advertiserId);

    /**
     *
     * @param campaignId
     * @param advertiserId
     * @return
     */
    @Query("SELECT c FROM Campaign c WHERE c.campaignId = :campaignId AND c.advertiser.advertiserId = :advertiserId")
    Optional<Campaign> findByCampaignIdAndAdvertiserId(@Param("campaignId") String campaignId,
            @Param("advertiserId") String advertiserId);

    /**
     *
     * @param campaignIds
     * @param advertiserId
     * @return
     */
    @Query("SELECT c FROM Campaign c WHERE c.campaignId IN :campaignIds AND c.advertiser.advertiserId = :advertiserId")
    List<Campaign> findByCampaignIdsAndAdvertiserId(@Param("campaignIds") List<String> campaignIds,
            @Param("advertiserId") String advertiserId);
}