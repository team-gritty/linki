package com.linki.admin_integration_service.domain.contract.repository.myBatis;

import com.linki.admin_integration_service.domain.contract.dto.ContractDTO;
import com.linki.admin_integration_service.domain.contract.dto.ContractSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractMapper {
    List<ContractDTO> getContracts();
    List<ContractDTO> searchContract(ContractSearchDTO contractSearchDTO);
}
