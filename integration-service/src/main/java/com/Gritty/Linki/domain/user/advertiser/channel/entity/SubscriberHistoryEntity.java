package com.Gritty.Linki.domain.user.advertiser.channel.entity;

import com.Gritty.Linki.entity.Channel;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 구독자 히스토리 엔티티
 */
@Entity
@Table(name = "subscriber_history")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(exclude = "channel") // 순환 참조 방지
public class SubscriberHistoryEntity {

    @Id
    @Column(name = "id", nullable = false, length = 25)
    private String id;

    // 연관관계 매핑: Channel 엔티티와 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Column(name = "subscriber_count", nullable = false)
    private Long subscriberCount;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    // 편의 메서드: channelId getter (기존 코드 호환성 유지)
    public String getChannelId() {
        return channel != null ? channel.getChannelId() : null;
    }
}