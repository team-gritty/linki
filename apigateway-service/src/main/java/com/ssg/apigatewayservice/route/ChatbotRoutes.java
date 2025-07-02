package com.ssg.apigatewayservice.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  Chatbot-service 라우트
 *
 * 1) REST  : /v1/chatbot-service/**  →  lb://CHATBOT-SERVICE
 *
 *  lb://  → Service Discovery(Eureka 등) 로드밸런싱
 */
@Configuration
public class ChatbotRoutes {

    @Bean
    public RouteLocator chatbotRoute(RouteLocatorBuilder b) {
        return b.routes()
                // ── 1) REST API
                .route("chatbot-api", r -> r
                        .path("/v1/chatbot/**")
                        .uri("lb://CHATBOT-SERVICE"))
                .build();
    }
}