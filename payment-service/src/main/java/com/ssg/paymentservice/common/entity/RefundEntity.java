package com.ssg.paymentservice.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refund")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundEntity{
    @Id
    @Column(name = "refund_id", length = 25)
    private String refundId;

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @Column(name = "complete_at", nullable = false)
    private LocalDateTime completeAt;

    @Column(name = "canceled_id", length = 255, nullable = false)
    private String canceledId;

    @Column(name = "refund_amount", nullable = false)
    private Integer refundAmount;

    @Column(name = "admin_id", length = 255, nullable = false)
    private String adminId;

    /* 연관 관계 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private PaymentEntity paymentEntity;
}