package com.ssg.paymentservice.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDto {
    private String billingId;
    private String billingKey;
    private String userId;
    private Boolean active;
    private Boolean autoBillingActivated;
    private LocalDateTime lastPaidAt;
    private LocalDateTime nextBillingAt;
    private Integer failCount;

    private String cardCompany;
    private String cardNumber;
    private String cardType;
    private String cardOwnerType;
    private String issuerCode;
    private String acquirerCode;
}
