package com.Gritty.Linki.domain.user.influencer.contract.service;

import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import org.springframework.stereotype.Service;


public interface AdvertiserContractService {
    String CreateContract(ContractCreateRequestDTO dto);
}
