package com.Gritty.Linki.domain.user.advertiser.channel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 구독자 수 조회 이력 엔티티
 */
@Entity
@Table(name = "subscriber_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberHistory {
    // 기본 키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채널 엔티티와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    // 구독자 수
    @Column(name = "subscriber_count", nullable = false)
    private Long subscriberCount;

    // 수집 날짜
    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    // 엔티티 생성시 현재 시간으로 수집 날짜 설정
    @PrePersist
    protected void onCreate() {
        collectedAt = LocalDateTime.now();
    }
}