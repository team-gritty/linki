package com.ssg.apigatewayservice.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubscribeRoutes {
    @Bean
    public RouteLocator subscribeRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("subscribe-service", r -> r
                        .path("/v1/subscribe-service/api/**")
                        .uri("lb://SUBSCRIBE-SERVICE"))
                .build();
    }
}
