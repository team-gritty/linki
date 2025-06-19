package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractStatusMapperDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate contractCreatedDate;
    private String contractStatus;
}
