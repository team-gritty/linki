package com.ssg.paymentservice.common.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "billing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BillingEntity {

    @Id
    @Column(name = "billing_id", length = 36)
    private String billingId;

    @Column(name = "billing_key", length = 100, nullable = false)
    private String billingKey;

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId;

    @Column(name = "billing_createat", nullable = false)
    private LocalDateTime billingCreateAt;

    @Column(name = "billing_status", nullable = false)
    private Boolean billingStatus;

    @Column(name = "payment_approve_status", nullable = false)
    private Boolean paymentApproveStatus;

    // 카드 관련 필드 추가
    @Column(name = "card_company", length = 20)
    private String cardCompany;

    @Column(name = "card_number", length = 30)
    private String cardNumber;

    @Column(name = "card_type", length = 10)
    private String cardType;

    @Column(name = "card_owner_type", length = 10)
    private String cardOwnerType;

    @Column(name = "issuer_code", length = 10)
    private String issuerCode;

    @Column(name = "acquirer_code", length = 10)
    private String acquirerCode;
}



