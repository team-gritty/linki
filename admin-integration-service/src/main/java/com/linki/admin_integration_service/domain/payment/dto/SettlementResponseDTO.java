package com.linki.admin_integration_service.domain.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SettlementResponseDTO {
    private String contractId;
    private String advertiserName;
    private String influencerName;
    private LocalDate adStartDate;
    private LocalDate adEndDate;
    private BigDecimal adAmount;
    private String isSettled;
}
