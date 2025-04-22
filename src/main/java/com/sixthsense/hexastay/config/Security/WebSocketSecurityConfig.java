package com.sixthsense.hexastay.config.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1) // 더 높은 우선순위

public class WebSocketSecurityConfig {

    @Bean
    public SecurityFilterChain webSocketFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/ws-order-alert/**", "/ws-order-alert/**/**") // 경로 포괄
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                )
                .csrf().disable(); // 웹소켓은 보통 CSRF 미적용

        return http.build();
    }

}
