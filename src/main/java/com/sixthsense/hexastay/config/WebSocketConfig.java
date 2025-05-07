package com.sixthsense.hexastay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.net.http.WebSocket;

@Configuration
@EnableWebSocketMessageBroker

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 WebSocket 엔드포인트
        registry.addEndpoint("/ws-order-alert") // 클라이언트 JavaScript에서 이 엔드포인트로 연결합니다.
                .setAllowedOriginPatterns("*") // 개발 중에는 모든 출처를 허용하지만, 프로덕션에서는 특정 도메인으로 제한하는 것이 좋습니다.
                .withSockJS(); // SockJS는 WebSocket을 지원하지 않는 브라우저에 대한 fallback 옵션을 제공합니다.
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // ✅ "/queue" 추가

        config.setApplicationDestinationPrefixes("/app");

        config.setUserDestinationPrefix("/user"); // ✅ 사용자 목적지 접두사 추가
    }

}
