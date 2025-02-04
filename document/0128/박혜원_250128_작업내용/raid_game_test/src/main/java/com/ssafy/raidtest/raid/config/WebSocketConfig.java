package com.ssafy.raidtest.raid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker// 웹소켓 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) { // 메시지 브로커 설정
        // 메시지 브로커가 구독자(server->clients)에게 메시지를 전달할 때 prefix 지정(topic:브로드 캐스트, queue:일대일메시징)
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app"); // client -> server로 메시지 보낼 때 사용할 prefix(ex. /app/game/distance로 메시지 보내면 서버의 distance메서드가 처리)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-raid") // WebSocket 연결 엔드포인트
                .setAllowedOrigins("*") // cors 설정(cors 방어 안함->모든 도메인에서 접속 허용)
                .withSockJS(); //sockJS 지원 활성화
    }
}
