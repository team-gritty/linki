package com.ssg.subscribeservice.entity;

import com.ssg.subscribeservice.subsenum.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_subscribe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscribeEntity {
    /** PK */
    @Id
    @Column(name = "user_subscribe_id", length = 36)
    private String userSubscriptionId;

    /** 구독 보유 사용자 */
    @Column(name = "user_id", length = 50)     // ← 접두사 유지
    private String userId;

    /** 어떤 플랜(SubscribeEntity)인지 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribe_id")                        // ← FK 컬럼은 예외 (그대로)
    private SubscribeEntity subscribe;

    /** 첫 결제 성공 시각 */
    @Column(name = "user_subscribe_started_at")
    private LocalDateTime userSubscribeStartedAt;

    /** 다음 결제 예정일 */
    @Column(name = "user_subscribe_next_billing_at")
    private LocalDateTime userSubscribeNextBillingAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_subscribe_status", length = 15)
    private SubscriptionStatus userSubscribeStatus;

    @Column(name = "user_subscribe_billing_registered")
    private Boolean userSubscribeBillingRegistered;
}

