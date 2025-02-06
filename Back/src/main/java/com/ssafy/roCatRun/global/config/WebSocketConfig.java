package com.ssafy.roCatRun.global.config;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.ssafy.roCatRun.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * WebSocketConfig.java
 * 웹소켓 서버 설정 클래스
 */
@org.springframework.context.annotation.Configuration
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig {
    @Value("${socket-server.host}")
    private String host;

    @Value("${socket-server.port}")
    private Integer port;

    private final JwtUtil jwtUtil;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();

        // 기본 설정
        config.setHostname(host);
        config.setPort(port);

        // Socket 설정
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        // 타임아웃 설정
        config.setPingTimeout(60000);
        config.setPingInterval(25000);
        config.setFirstDataTimeout(10000); // 업그레이드 타임아웃 대신 사용

        // 스레드 풀 설정
        config.setBossThreads(1);
        config.setWorkerThreads(8);

        // WebSocket 전송 설정
        config.setAllowCustomRequests(true);
        config.setTransports(Transport.WEBSOCKET);

        // 인증 리스너 설정
        config.setAuthorizationListener(handshakeData -> {
            try {
                String authHeader = handshakeData.getHttpHeaders().get("Authorization");
                log.info("Received Authorization header: {}", authHeader);

                if (authHeader == null || authHeader.isEmpty()) {
                    log.error("Authorization header is null or empty");
                    return AuthorizationResult.FAILED_AUTHORIZATION;
                }

                if (authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    boolean isValid = jwtUtil.validateToken(token);

                    if (!isValid) {
                        log.error("Token validation failed");
                        return AuthorizationResult.FAILED_AUTHORIZATION;
                    }

                    String userId = jwtUtil.extractUserId(token);
                    log.info("Extracted userId: {}", userId);

                    return userId != null ? AuthorizationResult.SUCCESSFUL_AUTHORIZATION : AuthorizationResult.FAILED_AUTHORIZATION;
                } else {
                    log.error("Invalid Authorization header format");
                    return AuthorizationResult.FAILED_AUTHORIZATION;
                }
            } catch (Exception e) {
                log.error("Authorization error", e);
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
        });

        // CORS 설정
        config.setOrigin("*");

        return new SocketIOServer(config);
    }
}