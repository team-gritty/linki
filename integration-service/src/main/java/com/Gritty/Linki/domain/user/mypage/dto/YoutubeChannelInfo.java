package com.Gritty.Linki.domain.user.mypage.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class YoutubeChannelInfo {
    private String id;
    private String description;
    private String publishedAt;
    private String customUrl;
    private String country;
    private Snippet snippet;

    @Data
    public static class Snippet {
        private String title;
        private Thumbnails thumbnails;
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
        return description;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getCustomUrl() {
        return customUrl;
    }

    public String getCountry() {
        return country;
    }
}

