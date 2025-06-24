package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String paymentId;
    private LocalDate paymentDate;
    private int amount;
}
