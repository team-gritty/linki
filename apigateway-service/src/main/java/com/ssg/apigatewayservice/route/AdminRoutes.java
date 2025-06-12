package com.ssg.apigatewayservice.route;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminRoutes {
    @Bean
    public RouteLocator adminRoute(RouteLocatorBuilder b) {
        return b.routes()
                .route("integration-admin", route -> route
                        .path("/v1/admin/api/**")
                        .uri("lb://INTEGRATION-SERVICE"))
                .build();
    }
}
