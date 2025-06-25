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

    /* 이 테이블 고유 데이터 – prefix 적용 */
    @Column(name = "payment_history_subscribe_code", length = 15, nullable = false)
    private String subscribeCode;

    @Column(name = "payment_history_amount", nullable = false)
    private Integer amount;

    @Column(name = "payment_history_paid_at", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "payment_history_success", nullable = false)
    private Boolean success;

    @Column(name = "payment_history_tx_key")
    private String transactionKey;

    @Column(name = "payment_history_fail_code")
    private String failCode;

    @Column(name = "payment_history_fail_msg")
    private String failMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_id", updatable = false)
    private BillingEntity billing;
}
