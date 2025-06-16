package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 채널 엔티티
 */
@Entity
@Table(name = "channel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Channel {

    // 채널 고유 식별자. null 불가.
    @Id
    @Column(name = "channel_id")
    private String channelId;

    // 채널 이름. null 불가.
    @Column(name = "channel_name", nullable = false)
    private String channelName;

    // 채널 유튜브 아이디. null 불가.
    @Column(name = "youtube_channel_id", nullable = false)
    private String youtubeChannelId;

    // 채널 URL. null 불가.
    @Column(name = "channel_url", nullable = false)
    private String channelUrl;

    // 채널 카테고리. null 불가.
    @Column(name = "channel_category", nullable = false)
    private String channelCategory;

    // 채널 국가. null 불가.
    @Column(name = "channel_country", nullable = false)
    private String channelCountry;

    @Column(name = "channel_description")
    private String channelDescription;

    @Column(name = "channel_thumbnail_url")
    private String channelThumbnailUrl;

    @Column(name = "subscriber_count")
    private Long subscriberCount;

    @Column(name = "video_count")
    private Integer videoCount;

    @Column(name = "view_count")
    private Long viewCount;

    // 채널 생성일 (YouTube 채널 생성일)
    @Column(name = "channel_createdAt", nullable = false)
    private LocalDateTime channelCreatedAt;

    // 채널 총 좋아요 수
    @Column(name = "like_count")
    private Long likeCount;

    // 채널 총 댓글 수
    @Column(name = "comment_count")
    private Long commentCount;

    // 채널 배너 URL - JSON 직렬화에만 포함되고 DB에는 저장되지 않음
    @Transient
    private String channelBannerUrl;

    @Column(name = "collected_at", nullable = false)
    private LocalDateTime collectedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "influencer_id", nullable = false)
    private Influencer influencer;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChannelStats> channelStats = new ArrayList<>();

    @OneToOne(mappedBy = "channel", cascade = CascadeType.ALL)
    private InfluencerAuth influencerAuth;

    @PrePersist
    public void prePersist() {
        this.collectedAt = LocalDateTime.now();
    }

}