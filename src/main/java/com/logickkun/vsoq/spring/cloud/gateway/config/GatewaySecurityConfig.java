package com.logickkun.vsoq.spring.cloud.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.net.URI;

@Slf4j
@EnableWebFluxSecurity
@Configuration
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(ServerHttpSecurity http) {
        return http
                // CSRF/Basic/FormLogin은 게C이트웨이에서 사용하지 않으므로 비활성화
                .cors(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .authorizeExchange(exchanges -> exchanges
                        // 0) CORS 프리플라이트: 브라우저의 사전검사(OPTIONS)는 무조건 통과시켜야
                        //    뒤따를 본요청(POST/GET with JSON)이 정상 진행됨.
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()

                        // 1) 공개 엔드포인트: 인증 없이 접근 허용
                        .pathMatchers(
                                // 로그인 초기 진입(게이트웨이가 index.html 서빙 → SPA가 라우팅)
                                "/login",

                                // 정적 파일(빌드 산출물): index.html, 번들/이미지/폰트 등
                                "/index.html", "/favicon.ico", "/vite.svg", "/assets/**",

                                // OIDC/JWKS/인증플로 관련 공개 엔드포인트
                                "/.well-known/**", "/oauth2/**",

                                // AuthZ 서버로 프록시되는 인증 관련 API (로그인/리프레시/로그아웃)
                                "/auth/login", "/auth/refresh", "/auth/logout"
                        ).permitAll()

                        // 2) 보호 리소스: 인증 필요
                        // 루트와 비즈니스 API는 토큰이 있어야 접근
                        .pathMatchers("/", "/api/**").authenticated()
                        // 위에서 명시하지 않은 나머지도 기본적으로 보호
                        .anyExchange().authenticated()
                )

                // 3) 인증 실패 처리 전략:
                //    - 페이지 네비게이션(HTML 문서 요청) → /login 으로 303 리다이렉트
                //    - XHR/Fetch(API) → 401 Unauthorized 반환 (프런트 인터셉터가 처리하기 좋게)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((exchange, e) -> {
                            var req = exchange.getRequest();
                            var res = exchange.getResponse();

                            var headers = req.getHeaders();
                            String dest = headers.getFirst("Sec-Fetch-Dest"); // document 이면 문서 네비게이션
                            String mode = headers.getFirst("Sec-Fetch-Mode"); // navigate 이면 문서 네비게이션
                            String accept = headers.getFirst(HttpHeaders.ACCEPT);       // text/html 포함 시 문서 요청으로 간주

                            // 최신 브라우저 기준 1순위: Sec-Fetch-* 헤더, 보조 판단: Accept 헤더
                            boolean isNavigation = "document".equals(dest) || "navigate".equals(mode);
                            boolean wantsHtml = accept != null && accept.contains("text/html");

                            if (isNavigation || wantsHtml) {
                                // 303 See Other: 이후 요청을 반드시 GET으로 전환하라는 의미(POST→GET 안전전환)
                                res.setStatusCode(HttpStatus.SEE_OTHER);
                                res.getHeaders().setLocation(URI.create("/login"));

                                log.info("GatewaySecurityConfig.authenticationEntryPoint() 303 See Other");

                                return res.setComplete();
                            } else {
                                // API/FETCH 요청에는 리다이렉트 대신 401을 내려 프론트가 프로그램적으로 대응하게 함
                                res.setStatusCode(HttpStatus.UNAUTHORIZED);

                                // 필요 시 간단 JSON 바디 내려주고 싶다면 아래 주석 해제
                                // byte[] body = "{\"message\":\"UNAUTHORIZED\"}".getBytes(StandardCharsets.UTF_8);
                                // DataBuffer buf = res.bufferFactory().wrap(body);
                                // res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                // return res.writeWith(Mono.just(buf));

                                return res.setComplete();
                            }
                        })
                )

                // 4) 게이트웨이에서 JWT 로컬 검증 사용
                //    - JWKS 설정은 application.yml에서 spring.security.oauth2.resourceserver.jwt.jwk-set-uri 로 지정
                //    - 또는 커스텀 ReactiveJwtDecoder 빈을 등록해서 사용
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
