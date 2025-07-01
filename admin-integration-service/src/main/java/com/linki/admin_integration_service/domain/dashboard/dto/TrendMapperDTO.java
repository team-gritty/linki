package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TrendMapperDTO {
    private LocalDate enterDay;
    private String role;
}
