package com.linki.admin_integration_service.domain.payment.repository.myBatis;

import com.linki.admin_integration_service.domain.payment.dto.SettlementDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SettlementMapper {
    List<SettlementDTO> getAllSettlements();
    List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO);
}
