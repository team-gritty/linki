package com.ssg.apigatewayservice.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationRoutes {
    @Bean
    public RouteLocator publicRoute(RouteLocatorBuilder b) {
        return b.routes()
                .route("integration-public", r -> r
                        .path("/v1/api/**")
                        .uri("lb://INTEGRATION-SERVICE"))
                .build();
    }
}
