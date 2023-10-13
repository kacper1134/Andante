package org.andante.config.security;

import lombok.RequiredArgsConstructor;
import org.andante.config.security.converter.KeycloakRealmRoleConverter;
import org.andante.config.security.role.KeycloakRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SecurityConfiguration {

    private final List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
    private final List<String> allowedOrigins = List.of("http://localhost:3000");
    private final List<String> allowedHeaders = List.of("Authorization", "Requestor-Type", "Content-Type");
    private final List<String> exposedHeaders = List.of("X-Get-Header");
    private final List<String> disabledSecurityEndpoints = List.of("/product/query", "/product/rating", "/product/bulk", "/product/popular",
            "/order/payment/webhook", "/activity/general", "/profile/image", "/activity/newsletter/subscribe", "/product/producer/top", "/product/comment/query", "/product/amplifier/query",
            "/product/headphones/query", "/product/subwoofer/query", "/product/speakers/query", "/product/gramophones/query", "/product/microphone/query", "/activity/prometheus", "/forum/prometheus",
            "/product/prometheus", "/order/prometheus");

    private final KeycloakRealmRoleConverter keycloakRealmRoleConverter;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .csrf().disable()
                .securityMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(disabledSecurityEndpoints.toArray(String[]::new) )))
                .authorizeExchange()
                    .pathMatchers("/product/comment/**").authenticated()
                    .pathMatchers("/product/status").authenticated()
                    .pathMatchers("/product/user").authenticated()
                    .pathMatchers(HttpMethod.POST, "/product/**").hasRole(KeycloakRole.ADMIN.getName())
                    .pathMatchers(HttpMethod.PUT, "/product/**").hasRole(KeycloakRole.ADMIN.getName())
                    .pathMatchers(HttpMethod.DELETE, "/product/**").hasRole(KeycloakRole.ADMIN.getName())
                    .pathMatchers(HttpMethod.POST, "/forum/topic/**").hasRole(KeycloakRole.ADMIN.getName())
                    .pathMatchers(HttpMethod.PUT, "/forum/topic/**").hasRole(KeycloakRole.ADMIN.getName())
                    .pathMatchers(HttpMethod.DELETE, "/forum/topic/**").hasRole(KeycloakRole.ADMIN.getName())
                    .anyExchange().authenticated()
                .and()
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                ));

         return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        allowedMethods.forEach(corsConfiguration::addAllowedMethod);
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.setAllowedHeaders(allowedHeaders);
        corsConfiguration.setExposedHeaders(exposedHeaders);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    private ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter jwtConverter = new ReactiveJwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(keycloakRealmRoleConverter);

        return jwtConverter;
    }
}
