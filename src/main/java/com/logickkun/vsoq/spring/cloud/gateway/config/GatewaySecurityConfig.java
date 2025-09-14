package com.logickkun.vsoq.spring.cloud.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

@EnableWebFluxSecurity
@Configuration
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // 최소화하려면 추후 /login POST만 예외로
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(
                                // DevTools(F12) Endpoint
                                "/.well-known/**",

                                // Login Form Request Endpoint
                                "/auth/login",

                                //BFF WEB Login Request Endpoint
                                "/bff/web/login", "/bff/web/callback",

                                //Authorization Server Endpoint
                                "/oauth2/authorize", "/oauth2/token", "/oauth2/revoke", "/oauth2/jwks", "/connect/register", "/connect/userinfo", "/connect/logout",

                                // Static resources
                                "/index.html","/assets/**","/favicon.ico","/vite.svg",

                                // Error Page
                                "/error"
                        ).permitAll()
                        .anyExchange().authenticated()
                )
                .build();
    }
}
