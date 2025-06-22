package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract,String> {
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

}
