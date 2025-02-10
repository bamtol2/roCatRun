package com.ssafy.roCatRun.domain.game.service.manager;

import com.corundumstudio.socketio.SocketIOServer;
import com.ssafy.roCatRun.domain.game.dto.response.GameOverResponse;
import com.ssafy.roCatRun.domain.game.dto.response.GameResultResponse;
import com.ssafy.roCatRun.domain.game.dto.response.PlayerLeftResponse;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.GameStatus;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import com.ssafy.roCatRun.domain.game.service.GameService;
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

    // 옵저버 패턴을 위한 리스너 인터페이스
    public interface GameTimeoutListener  {
        void onTimeout(GameRoom room);
    }

    private GameTimeoutListener timeoutListener;

    public void setTimeoutListener(GameTimeoutListener listener) {
        this.timeoutListener = listener;
    }


    public void startGameTimer(GameRoom room) {
        String roomId = room.getId();
        long timeLimit = room.getBossLevel().getTimeLimit(); // 초 단위
        room.setGameStartTime(System.currentTimeMillis());

        log.info("[Timer Start] Room: {}, Boss Level: {}, Time Limit: {}s",
                roomId, room.getBossLevel(), timeLimit);

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
        log.info("[Game Timeout] Room: {}, Players: {}, Final Boss Health: {}",
                room.getId(), room.getPlayers().size(), room.getBossHealth());

        if (timeoutListener != null) {
            timeoutListener.onTimeout(room);
        }

        cleanupTimerTasks(room.getId());

        // 타이머 정리
        cleanupTimerTasks(room.getId());
    }

    private void cleanupTimerTasks(String roomId) {
        ScheduledFuture<?> task = timerTasks.remove(roomId);
        if (task != null) {
            task.cancel(true);
        }
    }
}