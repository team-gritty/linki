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

    /**
     * 응답의 items 배열
     */
    @JsonProperty("items")
    private List<ChannelItem> items;

    /**
     * 채널 정보 (id, snippet, statistics)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelItem {
        @JsonProperty("id")
        private String id;

        @JsonProperty("snippet")
        private Snippet snippet; // 채널 메타 정보 (제목, 설명 등)

        @JsonProperty("statistics")
        private Statistics statistics; // 구독자 수, 조회 수, 영상 수

        @JsonProperty("brandingSettings")
        private BrandingSettings brandingSettings; // 배너 이미지 등
    }

    /**
     * 채널 메타 정보 (제목, 설명, 국가, 썸네일)
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Snippet {
        @JsonProperty("title")
        private String title; // 채널 이름

        @JsonProperty("description")
        private String description; // 채널 설명

        @JsonProperty("country")
        private String country; // 채널 국가 코드

        @JsonProperty("publishedAt")
        private String publishedAt; // 채널 생성일 (ISO 8601 날짜 문자열)

        @JsonProperty("thumbnails")
        private Thumbnails thumbnails; // 썸네일 이미지 (default 화질)
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
     * 썸네일 전체 정보
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

    /**
     * 브랜딩 설정 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandingSettings {
        // 이미지 관련 설정들
        @JsonProperty("image")
        private BrandingImage image;
    }

    /**
     * 브랜딩 이미지 정보
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BrandingImage {
        // 채널 배너 이미지 url
        @JsonProperty("bannerExternalUrl")
        private String bannerExternalUrl;
    }
}