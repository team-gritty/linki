package com.ssg.apigatewayservice.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  Chat-service 라우트
 *
 *  ┌─────────────────────────────────────────────┐
 *  │ 1) REST  : /v1/chat-service/api/**          │ →  lb://CHAT-SERVICE
 *  │ 2) WS    : /v1/chat-service/ws/**           │ →  lb:ws://CHAT-SERVICE
 *  │            & Upgrade: websocket 헤더        │                     │
 *  └─────────────────────────────────────────────┘
 *
 *  lb://  → Service Discovery(Eureka 등) 로드밸런싱
 *  lb:ws://→ 동일하지만 WebSocket 스킴으로 전달
 */
@Configuration
public class ChatRoutes {

    @Bean
    public RouteLocator chatRoute(RouteLocatorBuilder b) {
        return b.routes()
                // ── 1) REST API
                .route("chat-api", r -> r
                        .path("/v1/chat-service/api/**")
                        .uri("lb://CHAT-SERVICE"))

                // ── 2) WebSocket
                .route("chat-ws", r -> r
                        .path("/v1/chat-service/ws/**")
                        .and().header("Upgrade", "websocket")
                        .uri("lb:ws://CHAT-SERVICE"))
                .build();
    }
}