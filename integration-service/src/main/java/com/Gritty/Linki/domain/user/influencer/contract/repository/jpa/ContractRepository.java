package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.vo.enums.ContractStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract,String> {

    // 정산, 계약 상태가 완료된 리뷰 가능한 계약 조회
    @Query("""
SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO(
    c.contractId,
    c.contractTitle,
    c.contractStatus,
    c.contractStartDate,
    c.contractEndDate,
    c.contractAmount,
    ca.campaignName,
    s.settlementStatus
)
FROM Contract c
JOIN c.proposal p
JOIN p.campaign ca
JOIN Settlement s ON s.contract.contractId = c.contractId
WHERE p.influencer.influencerId = :influencerId
AND c.contractStatus = 'COMPLETED'
AND s.settlementStatus = 'COMPLETED'
""")
    List<ReviewableContractResponseDTO> findReviewableContractsByInfluencerId(@Param("influencerId") String influencerId);


    // 로그인한 인플루언서의 계약 목록 조회
    @Query("SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO(" +
            "c.contractId, c.contractTitle, c.contractStatus, c.contractStartDate, c.contractEndDate, " +
            "c.contractAmount, p.proposalId, camp.campaignName, c.adDeliveryStatus) " +
            "FROM Contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "WHERE p.influencer.influencerId = :influencerId AND c.contractStatus IN (:statuses)")
    List<ContractListResponseDTO> findContractsByInfluencerIdAndStatus(
            @Param("influencerId") String influencerId,
            @Param("statuses") List<ContractStatus> statuses
    );

    // 로그인 한 광고주의 계약 목록 조회
    @Query("SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO(" +
            "c.contractId, c.contractTitle, c.contractStatus, c.contractStartDate, c.contractEndDate, " +
            "c.contractAmount, p.proposalId, camp.campaignName, c.adDeliveryStatus) " +
            "FROM Contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "WHERE camp.advertiser.advertiserId = :advertiserId AND c.contractStatus IN (:statuses)")
    List<ContractListResponseDTO> findContractsByAdvertiserIdAndStatus(
            @Param("advertiserId") String advertiserId,
            @Param("statuses") List<ContractStatus> statuses
    );
}
