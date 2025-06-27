package com.ssg.paymentservice.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingInfoResponseDto {
    private LocalDateTime nextBillingAt;
    private String cardCompany;
    private String cardNumber;
    private Integer monthCount;
}
