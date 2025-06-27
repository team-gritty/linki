package com.ssg.paymentservice.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistoryEntity {
    /* PK */
    @Id
    @Column(name = "payment_history_id", length = 36)
    private String paymentHistoryId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "payment_history_order_id")
    private String orderId;

    @Column(name = "payment_history_subscribe_id", length = 100, nullable = false)
    private String subscribeId;

    @Column(name = "payment_history_amount", nullable = false)
    private Integer amount;

    @Column(name = "payment_history_paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "payment_history_success", nullable = false)
    private Boolean success;

    @Column(name = "payment_history_payment_key")
    private String paymentKey;

    @Column(name = "payment_history_fail_code")
    private String failCode;

    @Column(name = "payment_history_fail_msg")
    private String failMessage;
}
