package com.Gritty.Linki.domain.user.influencer.contract.service;

import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AdvertiserContractService {
    // 계약서 생성
    String CreateContract(ContractCreateRequestDTO dto);

    // 광고주 계약 목록 조회
    List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses);

    // 광고주 계약 상세조회
    ContractDetailResponseDTO getContractDetail(String contractId);
}
