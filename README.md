### vsoq-spring-authentication-server
## Spring cloude gateway<br>
# Dependencies

☑ Reactive Web (spring-boot-starter-webflux)
무엇인가?
스프링의 논블로킹 I/O 웹 서버(WebFlux)를 띄우기 위한 코어 의존성입니다.

왜 필요한가?
Spring Cloud Gateway 자체가 WebFlux 기반으로 동작하므로, Reactive Web이 없으면 게이트웨이가 실행되지 않습니다.
대규모 동시 연결을 효율적으로 처리하려면 반드시 논블로킹 스택이 필요합니다.

☑ Cloud Gateway (spring-cloud-starter-gateway)
무엇인가?
API Gateway 역할을 하는 라우터·필터·토큰 릴레이 기능을 제공하는 스타터입니다.

왜 필요한가?
MSA 진입점으로서 요청 경로별 라우팅, 헤더·쿼리 수정, CORS·인증·인가 필터 등을 쉽게 선언적으로 구현해 줍니다.

☑ OAuth2 Resource Server (spring-boot-starter-oauth2-resource-server)
무엇인가?
수신된 JWT(또는 opaque token)를 검증하고, 토큰의 스코프·클레임에 따라 접근을 허용·차단하는 기능을 제공합니다.

왜 필요한가?
게이트웨이 레벨에서 “유효한 토큰을 가진 요청만 뒤 서비스로 전달”하게 만들어, 보안을 중앙집중식으로 관리할 수 있습니다.

☑ OAuth2 Client (spring-boot-starter-oauth2-client)
무엇인가?
OAuth2 클라이언트 기능(인증 코드 플로우, 토큰 자동 갱신, Token Relay 등)을 지원합니다.

왜 필요한가?
게이트웨이가 백엔드 서비스 호출 시 클라이언트 자격증명을 사용해 토큰을 얻거나, 들어온 토큰을 그대로 전달(Relay)하려면 이 의존성이 필요합니다.

☑ Actuator (spring-boot-starter-actuator)
무엇인가?
헬스체크(/actuator/health), 메트릭(/actuator/metrics), 환경 설정 등 운영용 엔드포인트를 제공합니다.

왜 필요한가?
운영 환경에서 게이트웨이의 상태·메트릭을 조회하고, 장애 알림·자동 복구 파이프라인을 구성하려면 Actuator가 필수입니다.

☑ Micrometer – Prometheus (micrometer-registry-prometheus)
무엇인가?
Spring Actuator 메트릭을 Prometheus 포맷으로 노출해 주는 라이브러리입니다.

왜 필요한가?
게이트웨이의 CPU·메모리 사용량, HTTP 요청 지연·오류율 등 주요 지표를 Prometheus로 수집·시각화하기 위해 꼭 필요합니다.

☑ Cloud LoadBalancer (spring-cloud-starter-loadbalancer)
무엇인가?
서비스 디스커버리 없이도 Spring Cloud LoadBalancer를 이용해, URI별로 라운드로빈·가중치 분산을 해 주는 클라이언트 사이드 LB 구현체입니다.

왜 필요한가?
Eureka 등 외부 레지스트리가 없어도, 여러 인스턴스에 부하를 고르게 분산하거나 복수 엔드포인트 호출 시 안정성을 높일 수 있습니다.

☐ Eureka Discovery (optional)
무엇인가?
Netflix Eureka를 통한 서비스 등록·검색 기능을 제공합니다.

언제 쓰나?
MSA 전체가 Eureka를 사용 중이라면, Gateway도 Eureka 클라이언트로 등록해 라우팅 경로를 자동화할 수 있습니다.

☐ DevTools (optional)
무엇인가?
코드 변경 시 애플리케이션을 자동 재시작해 주는 개발 편의 도구입니다.

언제 쓰나?
로컬 개발 중 빠른 리로드가 필요할 때 유용하며, 운영 빌드에는 포함시키지 않습니다.

☐ Lombok (optional)
무엇인가?
Getter/Setter, 생성자, 빌더 등을 어노테이션만으로 자동 생성해 주는 라이브러리입니다.

언제 쓰나?
DTO·설정 클래스 등의 보일러플레이트를 줄이고 싶을 때, 팀 컨벤션에 맞춰 도입합니다.
