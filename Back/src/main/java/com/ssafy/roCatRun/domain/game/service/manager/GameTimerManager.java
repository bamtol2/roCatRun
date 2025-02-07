package com.ssafy.roCatRun.domain.game.service.manager;

import com.corundumstudio.socketio.SocketIOServer;
import com.ssafy.roCatRun.domain.game.dto.response.GameResultResponse;
import com.ssafy.roCatRun.domain.game.dto.response.PlayerLeftResponse;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.GameStatus;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameTimerManager {
    private final SocketIOServer server;
    private final GameRoomManager gameRoomManager;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, ScheduledFuture<?>> timerTasks = new ConcurrentHashMap<>();

    public void startGameTimer(GameRoom room) {
        String roomId = room.getId();
        long timeLimit = room.getBossLevel().getTimeLimit(); // 초 단위
        room.setGameStartTime(System.currentTimeMillis());

        // 제한 시간 종료 시 게임 종료
        ScheduledFuture<?> timerTask = scheduler.schedule(() -> {
            try {
                GameRoom currentRoom = gameRoomManager.getRoom(roomId)
                        .orElseThrow(() -> new IllegalStateException("Room not found"));

                if (currentRoom.getStatus() == GameStatus.PLAYING) {
                    handleGameTimeout(currentRoom);
                }
            } catch (Exception e) {
                log.error("게임 클리어 실패 처리 중 오류 발생, 방 {}: {}", roomId, e.getMessage());
            }
        }, timeLimit, TimeUnit.SECONDS);

        timerTasks.put(roomId, timerTask);
    }

    private void handleGameTimeout(GameRoom room) {
        room.setStatus(GameStatus.FINISHED);
        gameRoomManager.updateRoom(room);

        // 게임 실패 결과 생성 및 전송
        GameResultResponse result = createTimeoutGameResult(room);
        server.getRoomOperations(room.getId()).sendEvent("gameOver", result);

        // 방 제거 전에 플레이어들 정리
        for (Player player : room.getPlayers()) {
            cleanupPlayer(player.getId(), room);
        }

        // 타이머 정리
        cleanupTimerTasks(room.getId());
    }

    private void cleanupPlayer(String userId, GameRoom room) {
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
    }

    private GameResultResponse createTimeoutGameResult(GameRoom room) {
        List<GameResultResponse.PlayerResult> playerResults = room.getPlayers().stream()
                .map(player -> new GameResultResponse.PlayerResult(
                        player.getId(),
                        player.getRunningData().getDistance(),
                        player.getRunningData().getPace(),
                        player.getRunningData().getCalories(),
                        player.getUsedItemCount()
                ))
                .sorted((p1, p2) -> Double.compare(p2.getDistance(), p1.getDistance()))
                .collect(Collectors.toList());

        return new GameResultResponse(false, playerResults);
    }

    public void cancelGameTimer(String roomId) {
        ScheduledFuture<?> task = timerTasks.remove(roomId);
        if (task != null) {
            task.cancel(true);
        }
    }

    private void cleanupTimerTasks(String roomId) {
        ScheduledFuture<?> task = timerTasks.remove(roomId);
        if (task != null) {
            task.cancel(true);
        }
    }
}