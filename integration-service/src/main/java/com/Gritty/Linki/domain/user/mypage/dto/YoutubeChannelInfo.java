package com.Gritty.Linki.domain.user.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class YoutubeChannelInfo {
    private String id;
    private Snippet snippet;
    private String influencerId;

    @Data
    public static class Snippet {
        private String title;
        private Thumbnails thumbnails;
        private String description;
        private LocalDateTime publishedAt;
        private String customUrl;
        private String country;
        private String category;
    }

    @Data
    public static class Thumbnails {
        private ThumbnailInfo defaultThumbnail;

        @JsonProperty("default")
        public void setDefault(ThumbnailInfo defaultThumbnail) {
            this.defaultThumbnail = defaultThumbnail;
        }
    }

    @Data
    public static class ThumbnailInfo {
        private String url;
    }

    public String getChannelId() {
        return id;
    }

    public String getTitle() {
        return snippet.getTitle();
    }

    public String getThumbnailUrl() {
        return snippet.getThumbnails().getDefaultThumbnail().getUrl();
    }

    public String getDescription() {
        return snippet != null ? snippet.getDescription() : null;
    }

    public String getPublishedAt() {
        return snippet != null && snippet.getPublishedAt() != null
                ? snippet.getPublishedAt().toString()
                : null;
    }

    public String getCustomUrl() {
        return snippet != null ? snippet.getCustomUrl() : null;
    }

    public String getCountry() {
        return snippet != null ? snippet.getCountry() : null;
    }

    public String getcategory() {
        return snippet != null ? snippet.getCountry() : null;
    }

}

