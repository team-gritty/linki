package com.Gritty.Linki.domain.user.influencer.contract.repository.jpa;

import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;

@Repository

public interface ContractRepository extends JpaRepository<Contract,String> {
    Optional<Contract> findByProposal_ProposalId(String proposalId);


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
            "c.contractAmount, p.proposalId, camp.campaignId, camp.campaignName, c.adDeliveryStatus) " +
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
            "c.contractAmount, p.proposalId, camp.campaignId, camp.campaignName, c.adDeliveryStatus) " +
            "FROM Contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "WHERE camp.advertiser.advertiserId = :advertiserId AND c.contractStatus IN (:statuses)")
    List<ContractListResponseDTO> findContractsByAdvertiserIdAndStatus(
            @Param("advertiserId") String advertiserId,
            @Param("statuses") List<ContractStatus> statuses
    );

    // 인플루언서용 계약 상세 조회
    @Query("SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO(" +
            "c.contractId, c.contractTitle, c.contractStatus, c.contractStartDate, " +
            "c.contractEndDate, c.contractAmount, p.proposalId, camp.campaignId, camp.campaignName, c.adDeliveryStatus, " +
            "c.pdfFilePath, c.contractCreatedAt, c.contractCompletedAt, c.contractSpecialTerms, p.influencer.influencerId) " +
            "FROM Contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "WHERE c.contractId = :contractId AND p.influencer.influencerId = :influencerId")
    Optional<ContractDetailResponseDTO> findContractDetailForInfluencer(
            @Param("contractId") String contractId,
            @Param("influencerId") String influencerId);


    // 광고주용 계약 상세 조회
    @Query("SELECT new com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO(" +
            "c.contractId, c.contractTitle, c.contractStatus, c.contractStartDate, " +
            "c.contractEndDate, c.contractAmount, p.proposalId, camp.campaignId, camp.campaignName, c.adDeliveryStatus, " +
            "c.pdfFilePath, c.contractCreatedAt, c.contractCompletedAt, c.contractSpecialTerms, p.influencer.influencerId) " +
            "FROM Contract c " +
            "JOIN c.proposal p " +
            "JOIN p.campaign camp " +
            "JOIN camp.advertiser a " +
            "WHERE c.contractId = :contractId AND a.advertiserId = :advertiserId")
    Optional<ContractDetailResponseDTO> findContractDetailForAdvertiser(
            @Param("contractId") String contractId,
            @Param("advertiserId") String advertiserId);


    // 계약 상태 갱신
    @Modifying
    @Query("UPDATE Contract c SET c.contractStatus = 'COMPLETED', c.contractCompletedAt = :now " +
            "WHERE c.contractEndDate <= :today AND c.contractStatus != 'COMPLETED'")
    int updateExpiredContracts(@Param("today") LocalDate today, @Param("now") LocalDateTime now);

    //광고주 광고 이행 여부 확인
    @Modifying
    @Query("UPDATE Contract c SET c.adDeliveryStatus = true WHERE c.contractId = :contractId")
    int updateAdDeliveryStatusToCompleted(@Param("contractId") String contractId);

    // document 아이디로 계약 아이디 찾기
    Contract findByDocumentId(String documentId);


    boolean existsByProposal_ProposalId(String proposalId);




}
