package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 구독자 수 추이 엔티티
 */
@Entity
@Table(name = "subscriber_history")
@Getter
@Setter
@NoArgsConstructor
public class SubscriberHistory {

    @Id
    @Column(name = "history_id", length = 25)
    private String historyId;

    @Column(name = "subscriber_count", nullable = false)
    private Long subscriberCount;

    @Column(name = "video_count")
    private Integer videoCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "snapshot_date")
    private LocalDateTime snapshotDate;

    // 수집된 채널과 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collected_channel_id", nullable = false)
    private CollectedChannel collectedChannel;

    // 생성자
    public SubscriberHistory(String historyId, Long subscriberCount, CollectedChannel collectedChannel) {
        this.historyId = historyId;
        this.subscriberCount = subscriberCount;
        this.collectedChannel = collectedChannel;
    }

    // ID 설정 편의 메서드
    public void generateId() {
        if (this.historyId == null) {
            this.historyId = "HST-" + System.currentTimeMillis();
        }
    }
}