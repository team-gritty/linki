package com.Gritty.Linki.domain.user.influencer.contract.service;


import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;

import java.util.List;

public interface InfluencerContractService {
    // 인플루언서 계약 목록 조회
    List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses);

    // 인플루언서 계약 상세 조회
    ContractDetailResponseDTO getContractDetailForInfluencer(String contractId);

    // 계약상태 갱신
    int updateContractsToCompleted();
}
