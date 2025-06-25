package com.Gritty.Linki.domain.user.influencer.contract.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.UcanSign.UcanSignClient;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.entity.Contract;
import com.Gritty.Linki.exception.BusinessException;
import com.Gritty.Linki.exception.ErrorCode;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class InfluencerContractServiceImpl implements InfluencerContractService {
    private final ContractRepository contractRepository;
    private final AuthenticationUtil authenticationUtil;
    private final UcanSignClient ucanSignClient;

    @Override
    public List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses) {
        // 로그인한 인플루언서 ID 가져오기
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);

        // 계약 목록 조회
        return contractRepository.findContractsByInfluencerIdAndStatus(influencerId, statuses);
    }

    @Override
    public ContractDetailResponseDTO getContractDetailForInfluencer(String contractId) {
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return contractRepository
                .findContractDetailForInfluencer(contractId, influencerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRACT_NOT_FOUND));
    }

    @Override
    public int updateContractsToCompleted() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        int updatedCount = contractRepository.updateExpiredContracts(today, now);
        log.info("만료된 계약 상태 갱신 완료. 갱신된 건 수: {}", updatedCount);
        return updatedCount;
    }

    @Override
    public String viewDocument(String contractId) {
        // 1. 로그인한 인플루언서 ID 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);

        // 2. 계약 조회
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("계약을 찾을 수 없습니다"));

        // 3. 권한 확인 - 해당 계약의 인플루언서가 로그인한 인플루언서와 같은지 확인
        if (!contract.getProposal().getInfluencer().getInfluencerId().equals(influencerId)) {
            throw new AccessDeniedException("본인의 계약이 아닙니다.");
        }

        // 4. 유캔사인에서 문서 조회
        String documentId = contract.getDocumentId();
        return ucanSignClient.getDocument(documentId);
    }


}
