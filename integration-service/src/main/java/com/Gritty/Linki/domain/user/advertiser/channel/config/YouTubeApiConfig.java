package com.Gritty.Linki.domain.user.advertiser.channel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * YouTube API 설정 클래스
 */
@Configuration
public class YouTubeApiConfig {

    @Value("${YOUTUBE_API_KEY}")
    private String youtubeApiKey;

    /**
     * YouTube API 키 Getter
     * 
     * @return YouTube API 키
     */
    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }
}