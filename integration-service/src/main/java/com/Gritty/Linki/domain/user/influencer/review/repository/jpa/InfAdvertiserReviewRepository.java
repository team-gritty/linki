package com.Gritty.Linki.domain.user.influencer.review.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.InfAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.WrittenAdvertiserReviewResponseDTO;
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

    // 인플루언서가 작성한 광고주 리뷰 조회
    @Query("""
    SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.WrittenAdvertiserReviewResponseDTO(
        ar.advertiserReviewId,
        ar.advertiserReviewScore,
        ar.advertiserReviewComment,
        ar.advertiserReviewCreatedAt,
        c.contractId,
        ar.visibility,
        cam.campaignName
    )
    FROM AdvertiserReview ar
    JOIN ar.contract c
    JOIN c.proposal p
    JOIN p.campaign cam
    JOIN c.settlement s
    WHERE p.influencer.influencerId = :influencerId
    AND c.contractStatus = com.Gritty.Linki.vo.enums.ContractStatus.COMPLETED
    AND s.settlementStatus = com.Gritty.Linki.vo.enums.SettlementStatus.COMPLETED
""")
    List<WrittenAdvertiserReviewResponseDTO> findWrittenAdvertiserReviewsByInfluencerId(@Param("influencerId") String influencerId);

}
