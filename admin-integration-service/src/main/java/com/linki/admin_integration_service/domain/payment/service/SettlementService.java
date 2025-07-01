package com.linki.admin_integration_service.domain.payment.service;

import com.linki.admin_integration_service.domain.payment.dto.*;

import java.util.List;

public interface SettlementService {
    List<SettlementDTO> getAllSettlements();
    List<SettlementDTO> searchSettlement(SettlementSearchDTO settlementSearchDTO);
    Boolean approveSettlement(SettlementRequestDTO settlementRequestDTO);
    String exportExcel();



    SettlementKeysetResponseDTO getAllSettlementsWithKeyset(String cursor, int size);
    SettlementKeysetResponseDTO searchSettlementWithKeyset(SettlementSearchDTO searchDTO, String cursor, int size);
}

