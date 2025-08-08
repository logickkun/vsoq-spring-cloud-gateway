package com.logickkun.vsoq.spring.cloud.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
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
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(
                                // 미인증 로그인 관련 주소
                                "/login",
                                "/auth/login",

                                // Static resources
                                "/index.html",
                                "/vite.svg",
                                "/assets/**",
                                "/favicon.ico"
                        ).permitAll()
                        .pathMatchers("/", "/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                // 인증 실패 시 /login 으로 리다이렉트
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.TEMPORARY_REDIRECT);
                            exchange.getResponse()
                                    .getHeaders()
                                    .setLocation(URI.create("/login"));
                            return exchange.getResponse().setComplete();
                        })
                )
                // 4) JWT 리소스 서버 설정 (jwt() → jwt(withDefaults())) gateway level에서 토큰 검증을 해주는 역할.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }
}
