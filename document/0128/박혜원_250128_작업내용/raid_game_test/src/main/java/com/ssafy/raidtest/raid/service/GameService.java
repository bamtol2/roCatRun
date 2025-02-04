package com.ssafy.raidtest.raid.service;

import com.ssafy.raidtest.raid.domain.game.GameStatus;
import com.ssafy.raidtest.raid.domain.game.PlayerGameStatus;
import com.ssafy.raidtest.raid.domain.game.RaidGame;
import com.ssafy.raidtest.raid.domain.item.Item;
import com.ssafy.raidtest.raid.domain.room.RaidRoom;
import com.ssafy.raidtest.raid.domain.statistics.GameResult;
import com.ssafy.raidtest.raid.dto.PlayerStats;
import com.ssafy.raidtest.raid.dto.Reward;
import com.ssafy.raidtest.raid.dto.RewardType;
import com.ssafy.raidtest.raid.dto.message.FeverMessage;
import com.ssafy.raidtest.raid.dto.message.GameEndMessage;
import com.ssafy.raidtest.raid.dto.message.GameStartMessage;
import com.ssafy.raidtest.raid.exception.*;
import com.ssafy.raidtest.raid.memory.GameMemoryStore;
import com.ssafy.raidtest.raid.memory.RoomMemoryStore;
import com.ssafy.raidtest.raid.repository.ItemRepository;
import com.ssafy.raidtest.raid.repository.StatisticsRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameService {
    private final GameMemoryStore gameStore;
    private final RoomMemoryStore roomStore;
    private final ItemRepository itemRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final StatisticsRepository statisticsRepository;

    // ScheduledExecutorService를 static으로 변경하여 공유
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private static final int FEVER_DURATION_SECONDS = 30; // 피버타임 30초 설정

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
    /**
     * 게임 시작 처리
     * 1. 방 정보 확인
     * 2. 게임 초기화
     * 3. 3초 후 게임 시작
     */
    public void startGame(String roomId){
        // 룸 아이디로 레이드 룸 정보 가져오기
        RaidRoom room = roomStore.getRoom(roomId)
                .orElseThrow(()->new RoomNotFoundException(roomId));

        // 가져온 방의 플레이어 수 체크
        if(room.getPlayerIds().size()!=room.getMaxPlayers()){
            throw new GameStartException("유저 수가 부족합니다.");
        }

        // 게임 초기화
        RaidGame game = initializeGame(room);
        // 게임 정보 저장
        gameStore.saveGame(game.getGameId(), game);

        // 게임 ID를 로그로 출력
        log.debug("Created game with ID: {}", game.getGameId());

        /* ===== 중요: 비동기 게임 시작 처리 ===== */
        // 3초 후에 실행할 작업 예약
        scheduler.schedule(()->{
            try {
                // 3초 후 게임 정보 다시 조회
                RaidGame savedGame = gameStore.getGame(game.getGameId())
                        .orElseThrow(() -> new GameNotFoundException(game.getGameId()));
                // 게임 상태를 STARTED로 변경
                savedGame.setStatus(GameStatus.STARTED);
                gameStore.saveGame(savedGame.getGameId(), savedGame);
                // 클라이언트들에게 게임 시작 알림
                notifyGameStart(savedGame);
                log.debug("게임이 시작되었습니다: {}", savedGame.getGameId());
            }catch (Exception e){
                log.error("게임 시작 중 오류가 발생했습니다: {}", e.getMessage(), e);
            }
        }, 3, TimeUnit.SECONDS);
    }

    /**
     * 게임 초기화
     * - 새로운 게임 세션 생성
     * - 보스 정보 설정
     * - 플레이어 초기 상태 설정
     */
    public RaidGame initializeGame(RaidRoom room) {
        RaidGame game = new RaidGame();
        game.setGameId(UUID.randomUUID().toString()); // 게임 아이디 생성
        game.setRoomId(room.getRoomId()); // 방 정보를 바탕
        game.setBoss(room.getBoss()); // 보스 생성
        game.setCurrentBossHp(room.getBoss().getMaxHp());
        game.setStatus(GameStatus.INITIALIZING);
        game.setStartTime(LocalDateTime.now());
        game.setEndTime(LocalDateTime.now().plusMinutes(room.getBoss().getTimeLimit()));

        // 각 플레이어의 초기 상태 설정
        for (String playerId : room.getPlayerIds()) {
            PlayerGameStatus playerStatus = new PlayerGameStatus();
            playerStatus.setUserId(playerId);
            playerStatus.setDistance(0.0);
            playerStatus.setItemGauge(0.0);
            playerStatus.setItemUseCount(0);
            playerStatus.setDamageDealt(0.0);
            game.getPlayerStatuses().put(playerId, playerStatus);
        }
        return game;
    }

    // 게임 시작 알리기
    private void notifyGameStart(RaidGame game) {
        GameStartMessage message = GameStartMessage.builder()
                .gameId(game.getGameId())
                .boss(game.getBoss())
                .players(new ArrayList<>(game.getPlayerStatuses().keySet()))
                .startTime(game.getStartTime())
                .endTime(game.getEndTime())
                .build();

        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId() + "/start", message);

        // 각 플레이어에게 개별 알림
        game.getPlayerStatuses().keySet().forEach(playerId ->
                messagingTemplate.convertAndSendToUser(playerId, "/queue/game/start", message)
        );
    }

    /**
     * 실시간 거리 업데이트
     * - 플레이어의 달린 거리 업데이트
     * - 아이템 게이지 증가 처리
     */
    public void updateDistance(String gameId, String userId, double distance) {
        RaidGame game = gameStore.getGame(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        // 플레이어 상태 조회
        PlayerGameStatus playerStatus = game.getPlayerStatuses().get(userId);
        if (playerStatus == null) {
            throw new PlayerNotFoundException(userId);
        }

        // 거리 업데이트 및 게이지 증가
        playerStatus.setDistance(distance);
        updateItemGauge(playerStatus, distance);

        // 변경사항 저장 및 알림
        gameStore.saveGame(gameId, game);
        notifyGameUpdate(game);
    }

    private void updateItemGauge(PlayerGameStatus status, double distance) {
        // 거리에 따른 게이지 증가 (예: 100m당 10%)
        double gaugeIncrease = (distance - status.getDistance()) * 0.1;
        status.setItemGauge(Math.min(100, status.getItemGauge() + gaugeIncrease));
    }

    // 아이템 사용
    public void useItem(String gameId, String userId, Long itemId) {
        // 게임 조회
        RaidGame game = gameStore.getGame(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        // 플레이어 상태 조회
        PlayerGameStatus playerStatus = game.getPlayerStatuses().get(userId);
        if (playerStatus == null) {
            throw new PlayerNotFoundException(userId);
        }

        // 아이템 조회
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("아이템을 찾을 수 없습니다. itemId: " + itemId));

        // 플레이어가 해당 아이템을 가지고 있는지 확인
        if (!playerStatus.getItems().contains(item)) {
            throw new ItemNotAvailableException("사용 가능한 아이템이 아닙니다.");
        }

        // 아이템 사용 및 보스 데미지 처리
        applyDamage(game, item.getDamage());
        playerStatus.incrementItemUseCount();
        playerStatus.getItems().remove(item);  // 사용한 아이템 제거

        // 피버타임 체크
        checkFeverCondition(game);

        // 게임 종료 체크
        if (isBossDefeated(game)) {
            endGame(game, true);
        }

        // 상태 저장 및 업데이트 알림
        gameStore.saveGame(gameId, game);
        notifyGameUpdate(game);
    }

    private boolean isBossDefeated(RaidGame game) {
        return game.getCurrentBossHp() <= 0;
    }

    /**
     * 게임 종료 처리
     * - MVP 선정
     * - 결과 저장
     * - 리소스 정리
     * - 결과 통지
     */
    private void endGame(RaidGame game, boolean isVictory) {
        game.setStatus(GameStatus.ENDED);

        // MVP 선정
        String mvpUserId = determineMVP(game);

        // 결과 저장 및 정리
        saveGameResult(game, isVictory, mvpUserId);

        // 방 정리
        roomStore.removeRoom(game.getRoomId());
        gameStore.removeGame(game.getGameId());

        // 결과 통지
        notifyGameEnd(game, isVictory, mvpUserId);
    }

    private void saveGameResult(RaidGame game, boolean isVictory, String mvpUserId) {
        GameResult result = GameResult.builder()
                .gameId(game.getGameId())
                .playerIds(new ArrayList<>(game.getPlayerStatuses().keySet()))
                .bossId(game.getBoss().getId())
                .bossName(game.getBoss().getName())
                .victory(isVictory)
                .mvpUserId(mvpUserId)
                .clearTime(calculateClearTime(game))
                .playerStats(createPlayerStats(game))
                .playedAt(LocalDateTime.now())
                .playDate(LocalDate.now()) // playDate 추가
                .build();

        statisticsRepository.save(result);
    }

    private int calculateClearTime(RaidGame game) {
        return (int) ChronoUnit.SECONDS.between(game.getStartTime(), LocalDateTime.now());
    }

    private Map<String, PlayerStats> createPlayerStats(RaidGame game) {
        return game.getPlayerStatuses().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> PlayerStats.builder()
                                .distance(entry.getValue().getDistance())
                                .itemsUsed(entry.getValue().getItemUseCount())
                                .damageDealt(entry.getValue().getDamageDealt())
                                .build()
                ));
    }

    private void notifyGameEnd(RaidGame game, boolean isVictory, String mvpUserId) {
        GameEndMessage message = GameEndMessage.builder()
                .gameId(game.getGameId())
                .victory(isVictory)
                .mvpUserId(mvpUserId)
                .playerStats(createPlayerStats(game))
                .rewards(calculateRewards(game, isVictory))
                .build();

        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId() + "/end", message);
    }

    private Map<String, List<Reward>> calculateRewards(RaidGame game, boolean isVictory) {
        Map<String, List<Reward>> rewards = new HashMap<>();

        game.getPlayerStatuses().forEach((playerId, status) -> {
            List<Reward> playerRewards = new ArrayList<>();

            // 기본 경험치
            playerRewards.add(new Reward(RewardType.EXP, calculateBaseExp(game, isVictory)));

            // 승리 보상
            if (isVictory) {
                playerRewards.add(new Reward(RewardType.ITEM, generateVictoryItem()));
            }

            rewards.put(playerId, playerRewards);
        });

        return rewards;
    }

    private int calculateBaseExp(RaidGame game, boolean isVictory) {
        int baseExp = game.getBoss().getDifficulty() * 100;
        return isVictory ? baseExp : baseExp / 2;
    }


    private void applyDamage(RaidGame game, int damage) {
        int multiplier = game.isFeverMode() ? 2 : 1;
        game.setCurrentBossHp(game.getCurrentBossHp() - (damage * multiplier));
    }

    /**
     * 피버타임 조건 체크
     * - 모든 플레이어가 짝수 개의 아이템을 사용했는지 확인
     * - 피버타임 시작 조건 충족 시 피버모드 활성화
     */
    private void checkFeverCondition(RaidGame game) {
        boolean allUsedEvenItems = game.getPlayerStatuses().values().stream()
                .allMatch(status -> status.getItemUseCount() % 2 == 0 && status.getItemUseCount() > 0);

        if (allUsedEvenItems && !game.isFeverMode()) {
            startFeverMode(game);
        }
    }

    private void startFeverMode(RaidGame game) {
        game.setFeverMode(true);
        notifyFeverStart(game);
    }

    private void notifyFeverStart(RaidGame game) {
        FeverMessage message = FeverMessage.builder()
                .gameId(game.getGameId())
                .requiredDistance(calculateFeverDistance(game))
                .duration(FEVER_DURATION_SECONDS)
                .build();

        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId() + "/fever", message);
    }

    private double calculateFeverDistance(RaidGame game) {
        // 피버타임에 필요한 거리 계산 (예: 현재 가장 앞선 플레이어의 거리 + 100m)
        return game.getPlayerStatuses().values().stream()
                .mapToDouble(PlayerGameStatus::getDistance)
                .max()
                .orElse(0) + 100.0;
    }

    private String determineMVP(RaidGame game) {
        return game.getPlayerStatuses().entrySet().stream()
                .max(Comparator.comparingDouble(e -> e.getValue().getDamageDealt()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private void notifyGameUpdate(RaidGame game) {
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
    }

    private int generateVictoryItem() {
        // 임의의 아이템 ID 반환 (1~5 사이)
        return new Random().nextInt(5) + 1;
    }
}
