package com.Gritty.Linki.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate 설정 클래스
 * HTTP 클라이언트 설정 및 타임아웃 설정을 포함합니다.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * RestTemplate Bean 생성
     * YouTube API 호출 등 외부 API 통신에 사용됩니다.
     * 
     * @return 설정된 RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        // HTTP 요청 팩토리 설정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 연결 타임아웃 설정 (5초)
        factory.setConnectTimeout(5000);

        // 읽기 타임아웃 설정 (30초) - YouTube API 응답 시간을 고려
        factory.setReadTimeout(30000);

        return new RestTemplate(factory);
    }
}