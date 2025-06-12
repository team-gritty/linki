package com.ssg.apigatewayservice.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentRoutes {
    @Bean
    public RouteLocator paymentRoute(RouteLocatorBuilder b) {
        return b.routes()
                .route("payment-service", r -> r
                        .path("/v1/payment-service/api/**")
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }
}
