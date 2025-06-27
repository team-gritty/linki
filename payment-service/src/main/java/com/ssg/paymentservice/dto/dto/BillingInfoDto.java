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
public class BillingInfoDto {
    private LocalDateTime nextBillingAt;
    private String cardCompany;
    private String cardNumber;
    private Integer monthCount;
}
