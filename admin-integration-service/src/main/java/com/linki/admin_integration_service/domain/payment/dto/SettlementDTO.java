package com.linki.admin_integration_service.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SettlementDTO {
    private String contractId;
    private String advertiserName;
    private String influencerName;
    private LocalDate adStartDate;
    private LocalDate adEndDate;
    private BigDecimal adAmount;
    private String isSettled;
}
