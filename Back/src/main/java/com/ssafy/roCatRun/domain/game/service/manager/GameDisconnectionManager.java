package com.ssafy.roCatRun.domain.game.service.manager;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.ssafy.roCatRun.domain.game.dto.request.GameEndVoteRequest;
import com.ssafy.roCatRun.domain.game.dto.response.*;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.GameStatus;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import com.ssafy.roCatRun.domain.game.entity.raid.RunningData;
import com.ssafy.roCatRun.domain.game.service.GameService;
import com.ssafy.roCatRun.global.socket.SessionManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class GameDisconnectionManager {
    private final RedisTemplate<String, DisconnectedPlayerData> redisTemplate;
    private final SocketIOServer server;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final GameService gameService;
    private final GameRoomManager gameRoomManager;
    private final SessionManager sessionManager;
    private static final long RECONNECT_TIMEOUT=60;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DisconnectedPlayerData implements Serializable {
        private String roomId;
        private String userId;
        private RunningData lastRunningData;
        private int usedItemCount;
        private long disconnectionTime;
    }

    public void handlePlayerDisconnection(GameRoom room, String userId, String nickName){
       room.getPlayers().removeIf(player -> player.getId().equals(userId));

        if (room.getPlayers().isEmpty()) {
            gameRoomManager.removeRoom(room.getId());
        } else {
            gameRoomManager.updateRoom(room);
            // 남은 플레이어들에게 알림
            server.getRoomOperations(room.getId()).sendEvent("playerDisconnected",
                    new PlayerLeftResponse(
                            userId,
                            nickName,
                            room.getPlayers().size(),
                            room.getMaxPlayers()
                    )
            );
        }
        // 남은 플레이어들에게 알림
//        server.getRoomOperations(room.getId()).sendEvent("playerDisconnected",
//                new PlayerDisconnectedResponse(userId, RECONNECT_TIMEOUT));
    }

//    public void handlePlayerDisconnection(GameRoom room, String userId) {
//        Player player = room.getPlayerById(userId);
//        if (player == null) return;
//
//        // Redis에 연결 끊긴 플레이어 정보 저장
//        DisconnectedPlayerData disconnectedData = new DisconnectedPlayerData(
//                room.getId(),
//                userId,
//                player.getRunningData(),
//                player.getUsedItemCount(),
//                System.currentTimeMillis()
//        );
//
//        String redisKey = "disconnected:" + userId;
//        redisTemplate.opsForValue().set(redisKey, disconnectedData);
//        redisTemplate.expire(redisKey, RECONNECT_TIMEOUT, TimeUnit.SECONDS);
//
//        // 남은 플레이어들에게 알림
//        server.getRoomOperations(room.getId()).sendEvent("playerDisconnected",
//                new PlayerDisconnectedResponse(userId, RECONNECT_TIMEOUT));
//
//        // 3분 후 투표 시작 - Redis 데이터 체크 없이 바로 투표 시작
//        scheduler.schedule(() -> {
//            // 방이 아직 존재하고 게임 중인지 확인
//            GameRoom currentRoom = gameRoomManager.getRoom(room.getId()).orElse(null);
//            if (currentRoom != null && currentRoom.getStatus() == GameStatus.PLAYING) {
//                initiateGameEndVote(currentRoom);
//            }
//        }, RECONNECT_TIMEOUT, TimeUnit.SECONDS);
//    }

    public boolean handlePlayerReconnection(String userId, SocketIOClient client) {
        String redisKey = "disconnected:" + userId;
        DisconnectedPlayerData data = redisTemplate.opsForValue().get(redisKey);

        if (data == null || isReconnectTimeoutExpired(data.getDisconnectionTime())) {
            return false;
        }

        GameRoom room = gameRoomManager.getRoom(data.getRoomId())
                .orElse(null);

        if (room == null || room.getStatus() != GameStatus.PLAYING) {
            return false;
        }

        // 재접속 처리
        Player player = new Player(userId);
        player.setRunningData(data.getLastRunningData());
        player.setUsedItemCount(data.getUsedItemCount());
        room.addPlayer(player);

        // 클라이언트 정보 설정
        client.set("userId", userId);

        // 세션 관리를 위한 추가
        sessionManager.createSession(userId, client.getSessionId().toString());

        // Redis에서 데이터 삭제
        redisTemplate.delete(redisKey);

        // 방에 재진입
        client.joinRoom(room.getId());

        // 다른 플레이어들에게 알림
        server.getRoomOperations(room.getId()).sendEvent("playerReconnected",
                new PlayerReconnectedResponse(userId, player.getNickname()));

        return true;
    }

    private boolean isReconnectTimeoutExpired(long disconnectionTime) {
        return System.currentTimeMillis() - disconnectionTime > RECONNECT_TIMEOUT * 1000;
    }

    private void initiateGameEndVote(GameRoom room) {
        if (room.getStatus() != GameStatus.PLAYING) return;

        // 기존 투표 이벤트 리스너 제거 (중복 방지)
        server.removeAllListeners("gameEndVote");

        // 투표 시작 이벤트 발송
        server.getRoomOperations(room.getId()).sendEvent("gameEndVoteStarted",
                new GameEndVoteStartedResponse(30)); // 30초 투표 시간

        AtomicInteger yesVotes = new AtomicInteger(0);
        AtomicInteger totalVotes = new AtomicInteger(0);

        // 투표 처리용 이벤트 리스너 추가
        server.addEventListener("gameEndVote", GameEndVoteRequest.class,
                (client, data, ack) -> {
                    String voterId = client.get("userId");
                    if (voterId != null && room.getPlayerById(voterId) != null) {
                        if (data.isEndGame()) {
                            yesVotes.incrementAndGet();
                        }
                        totalVotes.incrementAndGet();

                        // 모든 플레이어가 투표했는지 확인
                        if (totalVotes.get() == room.getPlayers().size()) {
                            processVoteResult(room, yesVotes.get(), totalVotes.get());
                            // 투표 완료 후 리스너 제거
                            server.removeAllListeners("gameEndVote");
                        }
                    }
                });

        // 30초 후 투표 종료
        scheduler.schedule(() -> {
            processVoteResult(room, yesVotes.get(), totalVotes.get());
            // 투표 종료 후 리스너 제거
            server.removeAllListeners("gameEndVote");
        }, 30, TimeUnit.SECONDS);
    }


    private void processVoteResult(GameRoom room, int yesVotes, int totalVotes) {
        // 현재 방 상태 다시 확인
        GameRoom currentRoom = gameRoomManager.getRoom(room.getId()).orElse(null);
        if (currentRoom == null || currentRoom.getStatus() != GameStatus.PLAYING) {
            return;
        }

        // 아무도 투표를 안했거나, 일부만 투표했을 경우
        if (totalVotes < room.getPlayers().size()) {
            server.getRoomOperations(room.getId()).sendEvent("gameEndVoteResult",
                    new GameEndVoteResultResponse(false, "투표 시간이 초과되어 게임을 계속 진행합니다."));
            return;
        }

        // 모든 플레이어가 투표했을 때만 과반수 체크
        if (yesVotes > totalVotes / 2) {
            room.setStatus(GameStatus.FINISHED);
            server.getRoomOperations(room.getId()).sendEvent("gameEndVoteResult",
                    new GameEndVoteResultResponse(true, "과반수가 게임 종료에 찬성했습니다."));
            gameService.handleGameOver(room);
        } else {
            server.getRoomOperations(room.getId()).sendEvent("gameEndVoteResult",
                    new GameEndVoteResultResponse(false, "과반수가 게임 종료에 반대했습니다."));
        }

    }
}
