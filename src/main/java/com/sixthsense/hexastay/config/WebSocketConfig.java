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
            registry.addEndpoint("/ws-order-alert")
                    .setAllowedOriginPatterns("*") // CORS 허용: 운영 시 도메인 제한해도 됨
                    .withSockJS(); // SockJS fallback 지원
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            // 관리자 브라우저가 구독할 prefix (서버 → 브라우저 알림 전송)
            config.enableSimpleBroker("/topic");

            // 클라이언트가 메시지를 서버로 보낼 때 사용할 prefix
            config.setApplicationDestinationPrefixes("/app");
        }

}
