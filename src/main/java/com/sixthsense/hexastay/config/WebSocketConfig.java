package com.sixthsense.hexastay.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**************************************************
 * 클래스명 : WebSocketConfig
 * 기능   : 웹소켓 및 STOMP 메시징을 설정하는 클래스입니다. (WebSocketMessageBrokerConfigurer 구현)
 * 클라이언트가 웹소켓 연결을 맺을 수 있는 STOMP 엔드포인트를 등록하고,
 * 메시지 브로커(메시지 발행/구독 처리)의 동작 방식을 구성합니다.
 * 이를 통해 실시간 양방향 통신 기능을 애플리케이션에 제공합니다.
 * 작성자 : 김윤겸
 * 작성일 : 2025-04-21
 * 수정일 :
 * 주요 설정/메소드 : registerStompEndpoints, configureMessageBroker
 **************************************************/

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
        config.enableSimpleBroker("/topic", "/queue"); //

        config.setApplicationDestinationPrefixes("/app");

        config.setUserDestinationPrefix("/user");
    }

}
