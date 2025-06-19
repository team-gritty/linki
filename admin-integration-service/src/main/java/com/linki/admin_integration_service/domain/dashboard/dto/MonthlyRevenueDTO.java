package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MonthlyRevenueDTO {
    private String paymentId;
    private LocalDate paymentDate;
    private int amount;
}
