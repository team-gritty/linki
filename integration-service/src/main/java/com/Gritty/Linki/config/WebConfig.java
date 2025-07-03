package com.Gritty.Linki.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS 설정: 프론트엔드(Vue, React 등)와 연동 위해 전체 허용 (개발 중)
     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*") // 추후 'https://linki-client.com' 등으로 제한 가능
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//     }

    /**
     * 인터셉터 등록: JWT 토큰 검증 인터셉터 (현재 미적용)
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 이후 JwtInterceptor 도입 시 아래와 같이 추가 예정
        // registry.addInterceptor(jwtInterceptor)
        //         .addPathPatterns("/api/**")
        //         .excludePathPatterns("/api/auth/**", "/error");
    }
}
