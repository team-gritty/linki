package com.Gritty.Linki.dto.response;

import com.Gritty.Linki.entity.CollectedChannel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChannelResponse {
    private String channelId;
    private String youtubeChannelId;
    private String channelName;
    private String channelUrl;
    private String channelDescription;
    private String channelThumbnailUrl;
    private String channelCategory;
    private String channelCountry;
    private Long subscriberCount;
    private Integer videoCount;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime channelCreatedAt;
    private LocalDateTime collectedAt;
    private String searchKeyword;
    private ChannelStatisticsResponse statistics;

    public static ChannelResponse from(CollectedChannel channel) {
        return ChannelResponse.builder()
                .channelId(channel.getCollectedChannelId())
                .youtubeChannelId(channel.getYoutubeChannelId())
                .channelName(channel.getChannelName())
                .channelUrl(channel.getChannelUrl())
                .channelDescription(channel.getChannelDescription())
                .channelThumbnailUrl(channel.getChannelThumbnailUrl())
                .channelCategory(channel.getChannelCategory())
                .channelCountry(channel.getChannelCountry())
                .subscriberCount(channel.getSubscriberCount())
                .videoCount(channel.getVideoCount())
                .viewCount(channel.getViewCount())
                .likeCount(channel.getLikeCount())
                .commentCount(channel.getCommentCount())
                .channelCreatedAt(channel.getChannelCreatedAt())
                .collectedAt(channel.getCollectedAt())
                .searchKeyword(channel.getSearchKeyword())
                .statistics(ChannelStatisticsResponse.from(channel))
                .build();
    }
}