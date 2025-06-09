package com.Gritty.Linki.domain.user.advertiser.channel.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channel {
    // 테이블 고유 키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채널 ID (유튜브 제공)
    @Column(name = "channel_id", nullable = false, unique = true)
    private String channelId;

    // 채널 이름
    @Column(nullable = false)
    private String title;

    // 채널 설명
    @Column(length = 1000)
    private String description;

    // 썸네일 이미지 주소
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // 구독자 수
    @Column(name = "subscriber_count")
    private Long subscriberCount;

    // 영상 개수
    @Column(name = "video_count")
    private Long videoCount;

    // 조회수
    @Column(name = "view_count")
    private Long viewCount;

    // 생성 시 생성 시간 업데이트
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 업데이트 시 업데이트 시간 업데이트
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 카테고리
    @Column(name = "category", length = 100)
    private String category;

    // 생성 시 생성 시간 업데이트
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // 업데이트 시 업데이트 시간 업데이트
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}