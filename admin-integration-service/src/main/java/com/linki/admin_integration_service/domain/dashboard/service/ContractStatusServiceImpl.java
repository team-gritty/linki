package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.ContractStatusDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContractStatusServiceImpl implements ContractStatusService {
    @Override
    public ContractStatusDTO getContractStatus() {
        ContractStatusDTO contractStatusDTO = new ContractStatusDTO();
        contractStatusDTO.setActive(152);
        contractStatusDTO.setCompleted(15);
        contractStatusDTO.setPending(251);
        return contractStatusDTO;
    }



}
