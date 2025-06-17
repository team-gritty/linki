package com.Gritty.Linki.domain.user.advertiser.channel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * YouTube Channel API 응답을 위한 DTO 클래스
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeChannelDto {

    @JsonProperty("items")
    private List<ChannelItem> items;

    /**
     * 채널 아이템 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelItem {
        @JsonProperty("id")
        private String id;

        @JsonProperty("snippet")
        private Snippet snippet;

        @JsonProperty("statistics")
        private Statistics statistics;
    }

    /**
     * 채널 스니펫 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("country")
        private String country;

        @JsonProperty("publishedAt")
        private String publishedAt;

        @JsonProperty("thumbnails")
        private Thumbnails thumbnails;
    }

    /**
     * 채널 통계 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        @JsonProperty("subscriberCount")
        private String subscriberCount;

        @JsonProperty("videoCount")
        private String videoCount;

        @JsonProperty("viewCount")
        private String viewCount;
    }

    /**
     * 썸네일 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnails {
        @JsonProperty("default")
        private Thumbnail defaultThumbnail;
    }

    /**
     * 개별 썸네일 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Thumbnail {
        @JsonProperty("url")
        private String url;
    }
}