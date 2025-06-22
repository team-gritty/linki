package com.linki.admin_integration_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebConfig는 Spring MVC의 전역 설정을 담당하는 클래스입니다.
 * CORS 설정을 포함하며, 추후 글로벌 웹 설정을 확장할 수 있습니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 애플리케이션의 CORS(Cross-Origin Resource Sharing) 설정을 구성합니다.
     * 모든 출처와 기본 HTTP 메서드를 허용하도록 설정되어 있습니다.
     *
     * @param registry CORS 설정을 위한 레지스트리
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("*") // 모든 Origin 허용 (보안상 필요시 특정 도메인만)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(false); // 쿠키 안 쓸 거면 false
    }

    /**
     * (선택 사항) JWT 기반 보안 처리를 위한 인터셉터 등록 설정입니다.
     * JWT 기능이 구현되면 주석을 해제하여 적용할 수 있습니다.
     *
     * @param registry 인터셉터 등록을 위한 레지스트리
     */
    //TODO : JWT TOKEN 붙이고 주석 처리 해제
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new JwtInterceptor())
//                .addPathPatterns("/**"); // 모든 요청에 대해 검사
//    }
}
