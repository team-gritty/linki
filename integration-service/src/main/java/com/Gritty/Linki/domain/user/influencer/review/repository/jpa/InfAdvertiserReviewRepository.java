package com.Gritty.Linki.domain.user.influencer.review.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.InfAdvertiserReviewResponseDTO;
import com.Gritty.Linki.entity.AdvertiserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InfAdvertiserReviewRepository extends JpaRepository<AdvertiserReview, String> {
    // 계약 ID로 리뷰가 존재하는지 확인
    boolean existsByContract_ContractId(String contractId);

    // 특정 캠페인을 등록한 광고주 리뷰 리스트 조회
    @Query("SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.InfAdvertiserReviewResponseDTO(" +
            "r.advertiserReviewId,r.advertiserReviewScore, r.advertiserReviewComment, r.advertiserReviewCreatedAt, c.contractId,r.visibility) " +
            "FROM AdvertiserReview r " +
            "JOIN r.contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "WHERE camp.campaignId = :campaignId")
    List<InfAdvertiserReviewResponseDTO> findReviewsByCampaignId(@Param("campaignId") String campaignId);

}
