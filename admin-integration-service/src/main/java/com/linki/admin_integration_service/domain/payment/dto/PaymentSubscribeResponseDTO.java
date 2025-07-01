package com.linki.admin_integration_service.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubscribeResponseDTO {
    private String userId;
    private String memberType;
    private String name;
    private String loginId;
    private String phone;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
    private LocalDateTime previousPaymentDate;
    private LocalDateTime nextPaymentDate;
}
