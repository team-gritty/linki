package com.linki.admin_integration_service.domain.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentSubscribeResponseDTO {
    private String memberType;
    private String name;
    private String loginId;
    private String phone;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private LocalDateTime previousPaymentDate;
    private LocalDateTime nextPaymentDate;
}
