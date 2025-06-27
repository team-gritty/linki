package com.ssg.paymentservice.dto.requestdto;

import lombok.*;


/* 토스 자동결제 요청 body
{
 "customerKey":"pWJ7DL0CXMt3nyaGU9ium",
 "amount":4900,
 "orderId":"JkNQCeXRuR9S7kZzjJWWi",
 "orderName":"토스 프라임 구독",
 "customerEmail":"customer@email.com",
 "customerName":"박토스",
 "taxFreeAmount":0
 }
 */
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
