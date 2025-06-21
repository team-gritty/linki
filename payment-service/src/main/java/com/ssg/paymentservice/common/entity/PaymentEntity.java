package com.ssg.paymentservice.common.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity {

    @Id
    @Column(name = "payment_id", length = 25)
    private String paymentId;

    @Column(name = "payed_at", nullable = false)
    private LocalDateTime payedAt;

    @Column(name = "payment_method", length = 25, nullable = false)
    private String paymentMethod;

    @Column(name = "subscribe_id", length = 25, nullable = false)
    private String subscribeId;

    /* 연관 관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", nullable = false)
    private BillingEntity billingEntity;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private RefundEntity refundEntity;
}