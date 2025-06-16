package com.Gritty.Linki.domain.user.advertiser.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * YouTube Search API (채널검색하고 데이터 수집하는것) 응답을 위한 DTO 클래스
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTubeSearchDto {

    @JsonProperty("items")
    private List<SearchItem> items;

    /**
     * 검색 결과 아이템
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchItem {
        @JsonProperty("id")
        private Id id;

        @JsonProperty("snippet")
        private Snippet snippet;
    }

    /**
     * 검색 결과 ID 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Id {
        @JsonProperty("channelId")
        private String channelId;
    }

    /**
     * 검색 결과 스니펫 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        @JsonProperty("channelId")
        private String channelId;

        @JsonProperty("title")
        private String title;

        @JsonProperty("description")
        private String description;

        @JsonProperty("thumbnails")
        private Thumbnails thumbnails;
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