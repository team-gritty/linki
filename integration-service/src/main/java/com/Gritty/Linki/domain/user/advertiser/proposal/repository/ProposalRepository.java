package com.Gritty.Linki.domain.user.advertiser.proposal.repository;

import com.Gritty.Linki.client.chatClient.dto.InterfaceChatInfoDto;
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

        //캠페인 아이디로 chatInfo 조회
        @Query(value = """
           select u.user_id AS userId,
                   u.user_login_id AS userLoginId,
                   p.proposal_id AS proposalId
            from proposal p
            join campaign c on p.campaign_id = c.campaign_id
            join influencer i on i.influencer_id = p.influencer_id
            join user u on u.user_id = i.user_id
            where c.campaign_id = :campaignId
        """,nativeQuery = true)
        List<InterfaceChatInfoDto>findInfluencerChatInfoByCampaignId(@Param("campaignId") String campaignId);

        //유저 아이디(광고주)로 상대방 chatInfo 조회
        @Query(value = """
                SELECT u.user_id AS userId,
              u.user_login_id AS userLoginId,
              p.proposal_id AS proposalId
       FROM proposal p
       JOIN campaign c ON p.campaign_id = c.campaign_id
       JOIN advertiser a ON c.advertiser_id = a.advertiser_id
       JOIN influencer i ON p.influencer_id = i.influencer_id
       JOIN user u ON i.user_id = u.user_id
       WHERE a.user_id = :loginUserId
       """,nativeQuery = true)
        List<InterfaceChatInfoDto> findAdvertiserChatInfoByUserId(@Param("loginUserId") String loginUserId);

        //유저 아이디(인플루언서)로 상대방 chatInfo 조회
        @Query(value = """
       SELECT u.user_id AS userId,
       u.user_login_id AS userLoginId,
       p.proposal_id AS proposalId
        FROM proposal p
        JOIN campaign c ON p.campaign_id = c.campaign_id
        JOIN advertiser a ON c.advertiser_id = a.advertiser_id
        JOIN user u ON a.user_id = u.user_id
        WHERE p.influencer_id = (SELECT influencer_id FROM influencer WHERE user_id = :userId)
        """, nativeQuery = true)
        List<InterfaceChatInfoDto> findInfluencerChatInfoByUserId(@Param("userId") String influencerUserId);


}