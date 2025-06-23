package com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa;


import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;
import com.Gritty.Linki.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InfSettlementRepository extends JpaRepository<Settlement, String> {
    Optional<Settlement> findByContract_ContractId(String contractId);

    // 로그인한 인플루언서의 정산 목록 조회

    @Query("""
SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO(
    s.settlementId,
    s.contract.contractId,
    s.settlementAmount,
    s.settlementStatus,
    s.settlementDate,
    c.campaignName
)
FROM Settlement s
JOIN s.contract ct
JOIN ct.proposal p
JOIN p.campaign c
WHERE s.influencer.influencerId = :influencerId
  AND ct.contractStatus = 'COMPLETED'
""")
    List<SettlementResponseDTO> findAllByInfluencerIdAndCompletedContract(@Param("influencerId") String influencerId);

}
