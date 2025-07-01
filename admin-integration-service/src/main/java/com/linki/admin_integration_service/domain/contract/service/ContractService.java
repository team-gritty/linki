package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.ContractDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractKeysetResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractSearchDTO;

import java.util.List;

public interface ContractService {
    List<ContractDTO> getContracts();
    List<ContractDTO> searchContract(ContractSearchDTO contractSearchDTO);
    String exportExcel();

    ContractKeysetResponseDTO getContractsWithKeyset(String cursor, int size);
    ContractKeysetResponseDTO searchContractsWithKeyset(ContractSearchDTO contractSearchDTO,String cursor, int size);
}
