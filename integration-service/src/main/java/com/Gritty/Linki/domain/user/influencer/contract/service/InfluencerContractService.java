package com.Gritty.Linki.domain.user.influencer.contract.service;


import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;

import java.util.List;

public interface InfluencerContractService {
    List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses);
}
