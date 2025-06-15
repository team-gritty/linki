package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 채널 엔티티
 */
@Entity
@Table(name = "channel")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel {

    // 채널 고유 식별자. null 불가.
    @Id
    @Column(name = "channel_id", length = 25)
    private String channelId;

    // 채널 이름. null 불가.
    @Column(name = "channel_name", length = 255, nullable = false)
    private String channelName;

    // 채널 URL. null 불가.
    @Column(name = "channel_url", length = 255, nullable = false)
    private String channelUrl;

    // 채널 카테고리. null 불가.
    @Column(name = "channel_category", length = 100, nullable = false)
    private String channelCategory;

    // 채널 국가. null 불가.
    @Column(name = "channel_country", length = 100, nullable = false)
    private String channelCountry;

    // 채널 생성 시간. null 불가.
    @Column(name = "channel_createdAt", nullable = false)
    private LocalDateTime channelCreatedAt;

    // 생성자
    public Channel(String channelId, String channelName, String channelUrl,
            String channelCategory, String channelCountry,
            LocalDateTime channelCreatedAt) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
        this.channelCategory = channelCategory;
        this.channelCountry = channelCountry;
        this.channelCreatedAt = channelCreatedAt;
    }
}