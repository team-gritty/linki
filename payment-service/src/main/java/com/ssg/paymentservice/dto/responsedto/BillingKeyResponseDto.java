package com.ssg.paymentservice.dto.responsedto;

import lombok.*;

import java.time.ZonedDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingKeyResponseDto {
    private String mId;
    private String customerKey;
    private ZonedDateTime authenticatedAt;
    private String method;
    private String billingKey;
    private String cardCompany;
    private String cardNumber;
    private Card card;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Card {
        private String issuerCode;
        private String acquirerCode;
        private String number;
        private String cardType;
        private String ownerType;
    }
}
