package org.andante.config.security.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CrossOriginRequestSharingFilter implements WebFilter {

    private final String allowedMethods = String.join(", ", List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    private final String allowedOrigins = String.join(", ", List.of("http://localhost:3000"));
    private final String allowedHeaders = String.join(", ", List.of("Authorization", "Requestor-Type", "Content-Type"));
    private final String exposedHeaders = String.join(", ", List.of("X-Get-Header"));

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getResponse().getHeaders();

        if (!headers.containsKey("Vary")) {
            headers.addIfAbsent("Access-Control-Allow-Origin", allowedOrigins);
            headers.addIfAbsent("Access-Control-Allow-Methods", allowedMethods);
            headers.addIfAbsent("Access-Control-Allow-Headers", allowedHeaders);
            headers.addIfAbsent("Access-Control-Expose-Headers", exposedHeaders);
            headers.addIfAbsent("Access-Control-Allow-Credentials", "true");
        }

        return chain.filter(exchange);
    }
}
