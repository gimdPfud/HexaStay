package com.sixthsense.hexastay.config.Security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**************************************************
 * 클래스명 : WebSocketSecurityConfig
 * 기능   : 웹소켓 경로에 대한 Spring Security 설정을 담당하는 클래스입니다.
 * 특정 웹소켓 엔드포인트(/ws-order-alert/**)에 대해 CSRF 보호를 비활성화하고
 * 모든 요청을 허용하도록 구성합니다. @Order(1)을 통해 다른 Security 설정보다 높은 우선순위를 가집니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-22
 * 수정일 :
 * 주요 설정/Bean : webSocketFilterChain (SecurityFilterChain)
 **************************************************/

@Configuration
@Order(1) // 더 높은 우선순위
public class WebSocketSecurityConfig {
    @Bean
    public SecurityFilterChain webSocketFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/ws-order-alert/**", "/ws-order-alert/") // 경로 포괄
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf().disable(); // 웹소켓은 보통 CSRF 미적용

        return http.build();
    }

}
