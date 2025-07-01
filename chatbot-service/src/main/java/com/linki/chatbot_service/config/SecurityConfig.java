package com.linki.chatbot_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/v1/chatbot/**").permitAll()  // 챗봇 API 모든 접근 허용
                .requestMatchers("/actuator/**").permitAll()    // Actuator 엔드포인트 허용
                .anyRequest().permitAll()                      // 나머지도 모든 접근 허용
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");  // 모든 도메인 허용
        configuration.addAllowedHeader("*");         // 모든 헤더 허용
        configuration.addAllowedMethod("*");         // 모든 HTTP 메소드 허용
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}