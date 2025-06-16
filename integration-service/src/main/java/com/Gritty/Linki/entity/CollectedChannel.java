package com.Gritty.Linki.entity;

import com.Gritty.Linki.util.IdGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 수집된 채널 정보 엔티티
 */
@Entity
@Table(name = "collected_channels")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectedChannel {

    @Id
    @Column(name = "collected_channel_id", length = 25)
    private String collectedChannelId;

    @Column(name = "youtube_channel_id", length = 255, nullable = false)
    private String youtubeChannelId;

    @Column(name = "channel_name", length = 255, nullable = false)
    private String channelName;

    @Column(name = "channel_url", length = 255, nullable = false)
    private String channelUrl;

    @Column(name = "channel_description", columnDefinition = "LONGTEXT")
    private String channelDescription;

    @Column(name = "channel_thumbnail_url", length = 500)
    private String channelThumbnailUrl;

    @Column(name = "channel_category", length = 100, nullable = false)
    private String channelCategory;

    @Column(name = "channel_country", length = 100)
    private String channelCountry;

    @Column(name = "subscriber_count")
    private Long subscriberCount;

    @Column(name = "video_count")
    private Integer videoCount;

    @Column(name = "view_count")
    private Long viewCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "comment_count")
    private Long commentCount;

    @Column(name = "channel_created_at")
    private LocalDateTime channelCreatedAt;

    @CreationTimestamp
    @Column(name = "collected_at", nullable = false, updatable = false)
    private LocalDateTime collectedAt;

    @Column(name = "search_keyword", length = 255)
    private String searchKeyword;

    // 채널 배너 URL - JSON 직렬화에만 포함되고 DB에는 저장되지 않음
    @Transient
    private String channelBannerUrl;

    // 구독자 히스토리와 일대다 관계
    @OneToMany(mappedBy = "collectedChannel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriberHistory> subscriberHistories = new ArrayList<>();

    // 생성자
    public CollectedChannel(String collectedChannelId, String youtubeChannelId,
            String channelName, String channelUrl, String channelCategory) {
        this.collectedChannelId = collectedChannelId;
        this.youtubeChannelId = youtubeChannelId;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
        this.channelCategory = channelCategory;
    }

    // 구독자 히스토리 추가 편의 메서드
    public void addSubscriberHistory(SubscriberHistory history) {
        subscriberHistories.add(history);
        history.setCollectedChannel(this);
    }

    // 현재 채널 정보를 구독자 히스토리에 추가
    public SubscriberHistory createCurrentSnapshot() {
        SubscriberHistory history = new SubscriberHistory();
        history.setHistoryId(IdGenerator.subscriberHistoryId());
        history.setCollectedChannel(this);
        history.setSubscriberCount(this.subscriberCount != null ? this.subscriberCount : 0L);
        history.setVideoCount(this.videoCount != null ? this.videoCount : 0);
        history.setViewCount(this.viewCount != null ? this.viewCount : 0L);
        return history;
    }

    // 영상 한 개당 평균 조회수 계산
    public double getAverageViewsPerVideo() {
        return videoCount != null && videoCount > 0
                ? (double) viewCount / videoCount
                : 0.0;
    }

    // 구독자 수 대비 평균 조회수 비율 계산
    public double getViewsToSubscriberRatio() {
        return subscriberCount != null && subscriberCount > 0
                ? getAverageViewsPerVideo() / subscriberCount
                : 0.0;
    }

    // 영상 한 개당 평균 좋아요 수 계산
    public double getAverageLikesPerVideo() {
        return videoCount != null && videoCount > 0 && likeCount != null
                ? (double) likeCount / videoCount
                : 0.0;
    }

    // 영상 한 개당 평균 댓글 수 계산
    public double getAverageCommentsPerVideo() {
        return videoCount != null && videoCount > 0 && commentCount != null
                ? (double) commentCount / videoCount
                : 0.0;
    }

    // 조회수 대비 좋아요 비율 계산
    public double getLikeToViewRatio() {
        return viewCount != null && viewCount > 0 && likeCount != null
                ? (double) likeCount / viewCount
                : 0.0;
    }

    // 조회수 대비 댓글 비율 계산
    public double getCommentToViewRatio() {
        return viewCount != null && viewCount > 0 && commentCount != null
                ? (double) commentCount / viewCount
                : 0.0;
    }
}