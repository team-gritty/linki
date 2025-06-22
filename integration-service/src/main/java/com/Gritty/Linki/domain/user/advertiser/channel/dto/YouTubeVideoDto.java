package com.Gritty.Linki.domain.user.advertiser.channel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * YouTube Data API 영상 정보에 대한 응답을 위한 DTO
 * 채널의 영상 목록과 통계 정보를 포함
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeVideoDto {

    @JsonProperty("items")
    private List<VideoItem> items;

    @JsonProperty("nextPageToken")
    private String nextPageToken;

    /**
     * 영상 아이템
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoItem {
        @JsonProperty("id")
        private String id;

        @JsonProperty("statistics")
        private Statistics statistics;

        @JsonProperty("snippet")
        private Snippet snippet;
    }

    /**
     * 영상 통계 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        @JsonProperty("viewCount")
        private String viewCount;

        @JsonProperty("likeCount")
        private String likeCount;

        @JsonProperty("commentCount")
        private String commentCount;
    }

    /**
     * 영상 스니펫 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        @JsonProperty("publishedAt")
        private String publishedAt;

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;
    }
}