package com.ssafy.roCatRun.global.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.JsonObject;
import com.ssafy.roCatRun.domain.game.entity.game.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.manager.GameRoomManager;
import com.ssafy.roCatRun.domain.game.dto.request.AuthenticateRequest;
import com.ssafy.roCatRun.domain.game.dto.request.MatchRequest;
import com.ssafy.roCatRun.domain.game.dto.response.AuthResponse;
import com.ssafy.roCatRun.domain.game.dto.response.MatchStatus;
import com.ssafy.roCatRun.domain.game.service.GameService;
import com.ssafy.roCatRun.global.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketEventHandler {
    private final SocketIOServer server;
    private final SessionManager sessionManager;
    private final GameService gameService;
    private final GameRoomManager gameRoomManager;
    private final JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        server.addConnectListener(this::handleConnect);
        server.addDisconnectListener(this::handleDisconnect);

        // 유저 인증 이벤트
        server.addEventListener("authenticate", AuthenticateRequest.class,
                (client, data, ack) -> handleAuthentication(client, data));

        // 랜덤 매칭 이벤트
        server.addEventListener("randomMatch", MatchRequest.class,
                (client, data, ack) -> handleRandomMatch(client, data));

        // 매칭 취소 이벤트
        server.addEventListener("cancelMatch", JsonObject.class,
                (client, data, ack) -> handleCancelMatch(client));

        // 연결 상태 확인 이벤트
        server.addEventListener("ping", JsonObject.class,
                (client, data, ack) -> handlePing(client));

        server.start();
    }

    private void handleConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    private void handleDisconnect(SocketIOClient client) {
        String socketId = client.getSessionId().toString();
        sessionManager.getSession(socketId).ifPresent(session -> {
            gameService.handleUserDisconnect(session.getUserId());
            sessionManager.removeSession(socketId);
        });
        log.info("Client disconnected: {}", socketId);
    }

    private void handleAuthentication(SocketIOClient client, AuthenticateRequest data) {
        try {
            log.info("Received authentication request with token: {}", data.getToken());  // 추가
            // JWT 토큰 검증
            String userId = validateAndGetUserId(data.getToken());

            // 기존 세션이 있다면 제거
            sessionManager.getSessionByUserId(userId).ifPresent(oldSession -> {
                SocketIOClient oldClient = server.getClient(UUID.fromString(oldSession.getSocketId()));
                if (oldClient != null) {
                    oldClient.disconnect();
                }
            });

            // 새 세션 생성
            sessionManager.createSession(userId, client.getSessionId().toString());
            client.set("userId", userId);

            // 인증 성공 응답
            client.sendEvent("authenticated", new AuthResponse(true, null));

        } catch (Exception e) {
            client.sendEvent("authenticated", new AuthResponse(false, e.getMessage()));
            client.disconnect();
        }
    }

    private void handleRandomMatch(SocketIOClient client, MatchRequest request) {
        String userId = client.get("userId");
        if (userId == null) {
            client.sendEvent("matchError", "Not authenticated");
            return;
        }

        try {
            GameRoom room = gameService.findOrCreateRandomMatch(userId, request);
            client.joinRoom(room.getId());

            // 매칭 상태 전송
            client.sendEvent("matchStatus", new MatchStatus(
                    room.getId(),
                    room.getPlayers().size(),
                    room.getMaxPlayers()
            ));

        } catch (Exception e) {
            client.sendEvent("matchError", e.getMessage());
        }
    }

    private void handleCancelMatch(SocketIOClient client) {
        String userId = client.get("userId");
        if (userId == null) {
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try {
            gameRoomManager.findRoomByUserId(userId).ifPresent(room -> {
                room.getPlayers().removeIf(player -> player.getId().equals(userId));
                if (room.getPlayers().isEmpty()) {
                    gameRoomManager.removeRoom(room.getId());
                } else {
                    gameRoomManager.updateRoom(room);
                }
                client.leaveRoom(room.getId());
            });

            client.sendEvent("matchCancelled", "Successfully cancelled match");
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handlePing(SocketIOClient client) {
        String userId = client.get("userId");
        if (userId != null) {
            client.sendEvent("pong", new JsonObject()); // 빈 JsonObject 전송
        }
    }

    private String validateAndGetUserId(String token) {
        String userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            throw new RuntimeException("Invalid token: userId not found");
        }
        return userId;
    }
}
