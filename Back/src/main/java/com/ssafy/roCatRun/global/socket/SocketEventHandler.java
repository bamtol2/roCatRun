
package com.ssafy.roCatRun.global.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.JsonObject;
import com.ssafy.roCatRun.domain.game.dto.request.*;
import com.ssafy.roCatRun.domain.game.dto.response.*;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.service.manager.GameDisconnectionManager;
import com.ssafy.roCatRun.domain.game.service.manager.GameRoomManager;
import com.ssafy.roCatRun.domain.game.entity.raid.GameStatus;
import com.ssafy.roCatRun.domain.game.service.GameService;
import com.ssafy.roCatRun.global.security.jwt.JwtTokenProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocketEventHandler {
    private final SocketIOServer server;
    private final SessionManager sessionManager;
    private final GameService gameService;
    private final GameRoomManager gameRoomManager;
    private final GameDisconnectionManager disconnectionManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        server.addConnectListener(this::handleConnect);
        server.addDisconnectListener(this::handleDisconnect);

        // 유저 인증 이벤트
        server.addEventListener("authenticate", AuthenticateRequest.class,
                (client,  data, ack) -> handleAuthentication(client, data));

        // 비공개 방 생성 이벤트
        server.addEventListener("createRoom", CreateRoomRequest.class,
                (client, data, ack)->handleCreateRoom(client, data));

        // 초대 코드로 방 참여 이벤트
        server.addEventListener("joinRoom", JoinRoomRequest.class,
                (client, data,ack)->handleJoinRoom(client, data));

        // 랜덤 매칭 이벤트
        server.addEventListener("randomMatch", MatchRequest.class,
                (client, data, ack) -> handleRandomMatch(client, data));

        // 매칭 취소 이벤트
        server.addEventListener("cancelMatch", JsonObject.class,
                (client, data, ack) -> handleCancelMatch(client));


        // 실시간 러닝 데이터 업데이트 이벤트
        server.addEventListener("updateRunningData", RunningDataUpdateRequest.class,
                (client, data, ack) -> handleRunningDataUpdate(client, data));

        // 아이템 사용 이벤트
        server.addEventListener("useItem", JsonObject.class,
                (client, data, ack) -> handleItemUse(client));

        // 유저의 러닝 결과 데이터 수신 이벤트
        server.addEventListener("submitRunningResult", PlayerRunningResultRequest.class,
                (client, data, ack) -> handleRunningResult(client, data));

        // 연결 상태 확인 이벤트
        server.addEventListener("ping", JsonObject.class,
                (client, data, ack) -> handlePing(client));

        server.start();
    }

    private void handleRunningResult(SocketIOClient client, PlayerRunningResultRequest data) {
        String userId = client.get("userId");
        if (userId == null) {
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try {
            gameService.handleRunningResult(userId, data);
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handleRunningDataUpdate(SocketIOClient client, RunningDataUpdateRequest data) {
        String userId = client.get("userId");
        if (userId == null) {
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try {
            gameService.handleRunningDataUpdate(userId, data.getRunningData());
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handleItemUse(SocketIOClient client) {
        String userId = client.get("userId");
        if (userId == null) {
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try {
            gameService.handleItemUse(userId);
        } catch (Exception e) {
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handleJoinRoom(SocketIOClient client, JoinRoomRequest request) {
        String userId = client.get("userId");
        if(userId==null){
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try{
            GameRoom room = gameService.joinRoomByInviteCode(userId, request.getInviteCode());
            client.joinRoom(room.getId());

            // 방 참여 성공 응답
            client.sendEvent("roomJoined", new RoomJoinedResponse(
                    room.getId(),
                    room.getInviteCode(),
                    room.getPlayers().size(),
                    room.getMaxPlayers()
            ));

            // 같은 방의 다른 유저들에게 새 유저 입장 알림
            server.getRoomOperations(room.getId()).sendEvent("playerJoined", new PlayerJoinedResponse(
                    userId,
                    room.getPlayers().size(),
                    room.getMaxPlayers()
            ));

            // 게임 시작 조건 체크
            gameService.checkAndStartGame(room);
        }catch (Exception e){
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handleCreateRoom(SocketIOClient client, CreateRoomRequest request) {
        String userId = client.get("userId");
        if(userId==null){
            client.sendEvent("error", "Not authenticated");
            return;
        }

        try{
            GameRoom room = gameService.createPrivateRoom(userId, request);
            client.joinRoom(room.getId());


            client.sendEvent("roomCreated", new roomCreatedResponse(
                    room.getId(),
                    room.getInviteCode(),
                    room.getPlayers().size(),
                    room.getMaxPlayers()
            ));

            // 게임 시작 조건 체크
            gameService.checkAndStartGame(room);
        }catch (Exception e){
            client.sendEvent("error", e.getMessage());
        }
    }

    private void handleConnect(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    private void handleAuthentication(SocketIOClient client, AuthenticateRequest data) {
        try {
            String token = data.getToken();

            // 토큰 유효성 검증
            if (!jwtTokenProvider.validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            // Security Context와 동일한 방식으로 Authentication 객체 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            String userId = authentication.getName();

            if (userId == null) {
                throw new RuntimeException("User ID not found in token");
            }

            // 재접속 시도
//            boolean reconnected = disconnectionManager.handlePlayerReconnection(userId, client);

//            if (!reconnected) {
                // 일반적인 새 연결 처리
                handleNormalAuthentication(client, userId);
//            }

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
            client.sendEvent("matchStatus", new MatchStatusResponse(
                    room.getId(),
                    room.getPlayers().size(),
                    room.getMaxPlayers()
            ));

            // 같은 방의 다른 유저들에게 새 유저 입장 알림
            server.getRoomOperations(room.getId()).sendEvent("playerJoined", new PlayerJoinedResponse(
                    userId,
                    room.getPlayers().size(),
                    room.getMaxPlayers()));

            // 게임 시작 조건 체크
            gameService.checkAndStartGame(room);

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
                    // 남은 플레이어들에게 알림
                    server.getRoomOperations(room.getId()).sendEvent("playerLeft",
                            new PlayerLeftResponse(
                                    userId,
                                    room.getPlayers().size(),
                                    room.getMaxPlayers()
                            )
                    );
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
        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        // Security Context와 동일한 방식으로 인증 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        String userId = authentication.getName();

        if (userId == null) {
            throw new RuntimeException("Invalid token: userId not found");
        }
        return userId;
    }

    // SocketEventHandler.java의 handleDisconnect 메소드 수정
    private void handleDisconnect(SocketIOClient client) {
        String socketId = client.getSessionId().toString();
        String userId = client.get("userId"); // 직접 client에서 userId를 가져옴

        if (userId != null) {  // userId가 있는 경우에만 처리
            gameRoomManager.findRoomByUserId(userId).ifPresent(room -> {
                if (room.getStatus() == GameStatus.PLAYING) {
                    // 게임 중일 때는 연결 끊김 특수 처리
                    disconnectionManager.handlePlayerDisconnection(room, userId);
                } else {
                    // 게임 중이 아닐 때는 기존 로직대로 처리
                    handleNormalDisconnection(room, userId, socketId);
                }
            });

            sessionManager.removeSession(socketId);
        }
        log.info("Client disconnected: {}", socketId);
    }

    private void handleNormalAuthentication(SocketIOClient client, String userId) {
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
    }

    private void handleNormalDisconnection(GameRoom room, String userId, String socketId) {
        String roomId = room.getId();
        room.getPlayers().removeIf(player -> player.getId().equals(userId));

        if (room.getPlayers().isEmpty()) {
            gameRoomManager.removeRoom(roomId);
        } else {
            gameRoomManager.updateRoom(room);
            // 남은 플레이어들에게 알림
            server.getRoomOperations(roomId).sendEvent("playerLeft",
                    new PlayerLeftResponse(
                            userId,
                            room.getPlayers().size(),
                            room.getMaxPlayers()
                    )
            );
        }

        sessionManager.removeSession(socketId);
    }
}