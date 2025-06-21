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
    @Column(name = "billing_id", length = 25)
    private String billingId;

    @Column(name = "billing_key", length = 100, nullable = false)
    private String billingKey;

    @Column(name = "user_id", length = 25, nullable = false)
    private String userId;

    @Column(name = "billing_createat", nullable = false)
    private LocalDateTime billingCreateAt;

    @Column(name = "billing_status", nullable = false)
    private Boolean billingStatus;

    @Column(name = "payment_approve_status", nullable = false)
    private Boolean paymentApproveStatus;

    /* 연관 관계 */
    @OneToMany(mappedBy = "billing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentEntity> payments = new ArrayList<>();
}



