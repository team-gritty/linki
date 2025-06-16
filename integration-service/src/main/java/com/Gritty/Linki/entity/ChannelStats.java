package com.Gritty.Linki.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "channel_stats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChannelStats {

    @Id
    @Column(name = "stats_id", length = 25)
    private String statsId;

    @Column(name = "subscriber_count")
    private Integer subscriberCount;

    @Column(name = "num_of_videos")
    private Integer numOfVideos;

    @Column(name = "views_per_video")
    private Integer viewsPerVideo;

    @Column(name = "data_fetched_at", nullable = false)
    private LocalDateTime dataFetchedAt;

    @Column(name = "likes_per_video")
    private Integer likesPerVideo;

    @Column(name = "comments_per_video")
    private Integer commentsPerVideo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;


}