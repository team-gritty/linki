package com.linki.admin_integration_service.domain.dashboard.repository.myBatis;

import com.linki.admin_integration_service.domain.dashboard.dto.ContractStatusMapperDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ContractStatusMapper {
    List<ContractStatusMapperDTO> getContractStatusMapper();
}
