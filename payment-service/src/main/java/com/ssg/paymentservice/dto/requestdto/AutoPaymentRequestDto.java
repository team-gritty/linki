package com.ssg.paymentservice.dto.requestdto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoPaymentRequestDto {
    private String customerKey; // userId
    private Integer amount;
    private String orderId;
    private String orderName;
    private String customerEmail;
    private String customerName;
    private Integer taxFreeAmount;
}
