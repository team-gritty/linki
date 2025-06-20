package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.SettlementDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementRequestDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementSearchDTO;

import java.util.List;

public interface SettlementService {
    List<SettlementDTO> getAllSettlements();
    List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO);
    Boolean approveSettlement(SettlementRequestDTO settlementRequestDTO);
    String exportExcel();
}
