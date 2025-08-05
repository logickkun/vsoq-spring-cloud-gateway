package com.logickkun.vsoq.spring.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

@EnableWebFluxSecurity
@Configuration
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // 1) 로그인 UI와 정적 자원은 인증 없이 허용
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/login", "/assets/**").permitAll()
                        // 2) 루트, SPA 엔트리포인트, API는 인증 필요
                        .pathMatchers("/", "/index.html", "/css/**", "/js/**", "/api/**").authenticated()
                        .anyExchange().authenticated()
                )
                // 3) 인증 실패 시 /login 으로 리다이렉트
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
                // 5) CSRF 비활성화 (API 중심이면 disable 권장)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
