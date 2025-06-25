package com.ssg.paymentservice.common.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "billing")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class BillingEntity {

    @Id
    @Column(name = "billing_id", length = 36)
    private String billingId; // UUID

    @Column(name = "billing_key", length = 100, nullable = false)
    private String billingKey; // Toss 빌링키

    @Column(name = "user_id", length = 50, nullable = false, unique = true)
    private String userId; // 소유 사용자

    @Column(name = "billing_active", nullable = false)
    private Boolean active; // 빌링키 유효 여부

    @Column(name = "billing_auto_billing_activated", nullable = false)
    private Boolean autoBillingActivated; // 자동결제 ON/OFF

    @Column(name = "billing_last_paid_at")
    private LocalDateTime lastPaidAt; // 마지막 결제일시

    @Column(name = "billing_next_billing_at")
    private LocalDateTime nextBillingAt; // 다음 결제 예정일

    @Column(name = "billing_fail_count", nullable = false)
    private Integer failCount; // 실패 횟수

    @Column(name = "billing_card_company")
    private String cardCompany;

    @Column(name = "billing_card_number")
    private String cardNumber;

    @Column(name = "billing_card_type")
    private String cardType;

    @Column(name = "billing_card_owner_type")
    private String cardOwnerType;

    @Column(name = "billing_issuer_code")
    private String issuerCode;

    @Column(name = "billing_acquirer_code")
    private String acquirerCode;
}



