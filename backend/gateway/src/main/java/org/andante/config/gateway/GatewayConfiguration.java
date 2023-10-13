package org.andante.config.gateway;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GatewayConfiguration {

    private final TokenRelayGatewayFilterFactory filterFactory;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/activity/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://ACTIVITY"))
                .route(r -> r.path("/forum/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://FORUM"))
                .route(r -> r.path("/order/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://ORDERS"))
                .route(r -> r.path("/product/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://PRODUCT"))
                .route(r -> r.path("/profile/**")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://ACTIVITY"))
                .route(r -> r.path("/order")
                        .filters(f -> f.filter(filterFactory.apply()))
                        .uri("lb://ORDERS"))
                .build();
    }
}
