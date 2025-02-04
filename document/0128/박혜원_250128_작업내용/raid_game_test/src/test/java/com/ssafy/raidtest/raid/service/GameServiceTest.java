package com.ssafy.raidtest.raid.service;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import com.ssafy.raidtest.raid.domain.game.GameStatus;
import com.ssafy.raidtest.raid.domain.game.RaidGame;
import com.ssafy.raidtest.raid.domain.room.RaidRoom;
import com.ssafy.raidtest.raid.domain.room.RoomStatus;
import com.ssafy.raidtest.raid.dto.message.GameStartMessage;
import com.ssafy.raidtest.raid.memory.GameMemoryStore;
import com.ssafy.raidtest.raid.memory.RoomMemoryStore;
import com.ssafy.raidtest.raid.repository.ItemRepository;
import com.ssafy.raidtest.raid.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// 단위 테스트에 공통적으로 사용할 확장 기능을 선언
@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    private GameMemoryStore gameStore;

    @Mock
    private RoomMemoryStore roomStore;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    // @Mock 또는 @Spy로 생성된 가짜 객체를 자동으로 주입시켜주는 객체
    @InjectMocks
    private GameService gameService;

    /**
     * 게임 시작 테스트
     * 조건: 플레이어가 모두 모였을 때
     * 결과: 3초 후 게임 시작
     */
    @Test
    void shouldStartGame() throws InterruptedException {
        // given
        RaidRoom room = createTestRoom(4);
        AtomicReference<String> savedGameId = new AtomicReference<>();

        when(roomStore.getRoom(room.getRoomId()))
                .thenReturn(Optional.of(room));

        // when
        gameService.startGame(room.getRoomId());

        // then
        // 3.5초 대기
        Thread.sleep(3500);

        // getGame과 saveGame 호출 확인
        verify(gameStore).saveGame(anyString(), argThat(game ->
                game.getStatus() == GameStatus.INITIALIZING));
        verify(gameStore).getGame(anyString());
        verify(gameStore).saveGame(anyString(), argThat(game ->
                game.getStatus() == GameStatus.STARTED));

        // 메시지 전송 확인
        verify(messagingTemplate).convertAndSend(
                matches("/topic/game/.+/start"),
                any(GameStartMessage.class)
        );

        // 각 플레이어에게 보내는 개별 메시지 확인
        for(int i = 1; i <= 4; i++) {
            String userId = "user" + i;
            verify(messagingTemplate).convertAndSendToUser(
                    eq(userId),
                    eq("/queue/game/start"),
                    any(GameStartMessage.class)
            );
        }
    }

    /**
     * 테스트용 방 생성
     * @param playerCount 참여 플레이어 수
     * @return 생성된 방 객체
     */
    private RaidRoom createTestRoom(int playerCount){
        RaidRoom room = new RaidRoom();
        room.setRoomId(UUID.randomUUID().toString());
        room.setBoss(createTestBoss());
        room.setMaxPlayers(playerCount);
        room.setStatus(RoomStatus.WAITING);

        for(int i=0; i<playerCount; i++){
            room.getPlayerIds().add("user"+(i+1));
        }

        return room;
    }

    /**
     * 테스트용 보스 생성
     * @return 테스트 보스 객체
     */
    private Boss createTestBoss(){
        Boss boss = new Boss();
        boss.setId(1L);
        boss.setName("사채업자 해파리");
        boss.setMaxHp(1000);
        boss.setDifficulty(1);
        boss.setTimeLimit(5);
        return boss;
    }

    // GameService의 initializeGame 메서드를 public으로 변경하면 아래 테스트도 추가 가능
    @Test
    void shouldInitializeGameCorrectly() {
        // given
        RaidRoom room = createTestRoom(4);

        // when
        RaidGame game = gameService.initializeGame(room);

        // then
        assertNotNull(game.getGameId());
        assertEquals(room.getRoomId(), game.getRoomId());
        assertEquals(room.getBoss(), game.getBoss());
        assertEquals(room.getBoss().getMaxHp(), game.getCurrentBossHp());
        assertEquals(GameStatus.INITIALIZING, game.getStatus());
        assertEquals(4, game.getPlayerStatuses().size());

        // 각 플레이어의 초기 상태 확인
        game.getPlayerStatuses().values().forEach(status -> {
            assertEquals(0.0, status.getDistance());
            assertEquals(0.0, status.getItemGauge());
            assertEquals(0, status.getItemUseCount());
            assertEquals(0.0, status.getDamageDealt());
        });
    }
}