package com.ssafy.roCatRun.global.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.ssafy.roCatRun.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * SocketConfig.java
 * 웹소켓 서버 설정 클래스
 */
@org.springframework.context.annotation.Configuration
@Slf4j
@RequiredArgsConstructor
public class SocketConfig {
    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    private final JwtUtil jwtUtil;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // WebSocket 연결 관련 타임아웃 설정
        config.setPingTimeout(60000); // 핑 타임아웃 60초
        config.setPingInterval(25000); // 핑 간격 25초
        config.setUpgradeTimeout(10000); // 업그레이드 타임아웃 10초

        // 스레드 풀 설정
        config.setBossThreads(1); // 메인 이벤트 루프 스레드
        config.setWorkerThreads(8); // 작업 처리 스레드

        // WebSocket을 유일한 전송 방식으로 설정
        config.setAllowCustomRequests(true);
        config.setTransports(new Transport[] { Transport.WEBSOCKET });

        // 인증 리스너 설정
        config.setAuthorizationListener(handshakeData -> {
            try {
                String token = handshakeData.getSingleUrlParam("token");
                log.info("Received token: {}", token); // 토큰 로깅

                if (token == null || token.isEmpty()) {
                    log.error("Token is null or empty");
                    return false;
                }

                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                boolean isValid = jwtUtil.validateToken(token);
                log.info("Token validation result: {}", isValid); // 검증 결과 로깅

                 if (!isValid) {
                    log.error("Token validation failed");
                    return false;
                }

                String userId = jwtUtil.extractUserId(token);
                log.info("Extracted userId: {}", userId); // 추출된 userId 로깅

                return userId != null;

            } catch (Exception e) {
                log.error("Authorization error", e); // 상세 에러 로깅
                return false;
            }
        });

        // cors 설정
        config.setOrigin("*");

        return new SocketIOServer(config);
    }
}