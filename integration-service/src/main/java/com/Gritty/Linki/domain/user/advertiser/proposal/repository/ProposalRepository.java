package com.Gritty.Linki.domain.user.advertiser.proposal.repository;

import com.Gritty.Linki.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 광고주 - 제안서 JPA 레포지토리
 */
@Repository
public interface ProposalRepository extends JpaRepository<Proposal, String> {

        /**
         * 제안서 ID와 광고주 ID로 제안서 조회 (광고주 권한 확인용)
         * Influencer의 User 정보도 함께 페치
         * 
         * @param proposalId   제안서 ID
         * @param advertiserId 광고주 ID
         * @return 제안서 Optional
         */
        @Query("SELECT p FROM Proposal p " +
                        "JOIN FETCH p.campaign c " +
                        "JOIN FETCH p.influencer i " +
                        "JOIN FETCH i.user u " +
                        "WHERE p.proposalId = :proposalId AND c.advertiser.advertiserId = :advertiserId")
        Optional<Proposal> findByProposalIdAndAdvertiserId(@Param("proposalId") String proposalId,
                        @Param("advertiserId") String advertiserId);

        /**
         * 캠페인 ID와 광고주 ID로 제안서 목록 조회 (권한 확인 포함)
         * Influencer의 User 정보도 함께 페치
         * 
         * @param campaignId   캠페인 ID
         * @param advertiserId 광고주 ID
         * @return 제안서 목록
         */
        @Query("SELECT p FROM Proposal p " +
                        "JOIN FETCH p.influencer i " +
                        "JOIN FETCH i.user u " +
                        "JOIN FETCH p.campaign c " +
                        "WHERE p.campaign.campaignId = :campaignId AND c.advertiser.advertiserId = :advertiserId")
        List<Proposal> findByCampaignIdAndAdvertiserId(@Param("campaignId") String campaignId,
                        @Param("advertiserId") String advertiserId);
}