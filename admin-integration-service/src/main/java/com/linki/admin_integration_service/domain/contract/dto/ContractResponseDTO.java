package com.linki.admin_integration_service.domain.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractResponseDTO {
    private String contractId;
    private LocalDate adStartDate;
    private LocalDate adEndDate;
    private BigDecimal contractAmount;
    private LocalDate paymentDate;
    private String influencerName;
    private String advertiserName;
    private String contractStatus;
    private String contractLink;
}
