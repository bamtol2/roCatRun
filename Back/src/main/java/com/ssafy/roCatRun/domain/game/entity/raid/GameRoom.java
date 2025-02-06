package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 개별 방의 상태와 플레이어 관리
 */
@Data
@AllArgsConstructor
public class GameRoom {
    public static final int ITEM_DAMAGE = 400;          // 아이템 기본 공격력
    public static final int FEVER_TIME_DURATION = 30;   // 피버타임 지속시간(초)
    public static final int REQUIRED_ITEMS_FOR_FEVER = 2; // 피버타임 발동을 위한 아이템 사용 횟수
    private String id;
    private String inviteCode;
    private BossLevel bossLevel;
    private int maxPlayers;
    private boolean isRandomMatch;
    private boolean isPrivate;
    private GameStatus status = GameStatus.WAITING;
    private List<Player> players = new ArrayList<>();
    private int bossHealth;
    private boolean feverTimeActive = false;
    private Long feverTimeEndAt;
    private Long gameStartTime;

    // 기본 생성자
    public GameRoom() {
        this.players = new ArrayList<>();
        this.status = GameStatus.WAITING;
        this.feverTimeActive = false;
    }

    // 필수 필드만 받는 생성자
    public GameRoom(String id, BossLevel bossLevel, int maxPlayers, boolean isRandomMatch) {
        this.id = id;
        this.bossLevel = bossLevel;
        this.maxPlayers = maxPlayers;
        this.isRandomMatch = isRandomMatch;
        this.players = new ArrayList<>();
        this.status = GameStatus.WAITING;
        this.bossHealth = bossLevel.calculateInitialHp(maxPlayers);
        this.feverTimeActive = false;
    }

    // 유저 아이디로 유저 상세 정보 가져오기
    public Player getPlayerById(String userId) {
        return players.stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    // 유저 추가
    public boolean addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            return false;
        }
        return players.add(player);
    }

    public void removePlayer(String userId) {
        players.removeIf(player -> player.getId().equals(userId));
    }

    // 게임 시작
    public void startGame() {
        this.status = GameStatus.PLAYING;
        this.gameStartTime = System.currentTimeMillis();
    }

    public boolean isGameReady() {
        return players.size() == maxPlayers;
    }

    // 피버 타임 발동 조건 확인
    public boolean checkFeverCondition() {
        // 이미 피버 상태면 중복 발동 X
        if (feverTimeActive) return false;

        // 모든 플레이어가 REQUIRED_ITEMS_FOR_FEVER의 배수만큼 아이템을 사용했는지 확인
        return players.stream().allMatch(p ->
                p.getItemCountForFever() >= REQUIRED_ITEMS_FOR_FEVER);
    }

    // 피버 타임 시작
    public void startFeverTime() {
        this.feverTimeActive = true;
        // 지금으로부터 피버 종료 시간 설정
        this.feverTimeEndAt = System.currentTimeMillis() + (FEVER_TIME_DURATION * 1000);
        // 피버타임 시작시 모든 플레이어의 아이템 사용 카운트 초기화
        players.forEach(Player::resetFeverItemCount);
    }

    // 피버타임 종료
    public void endFeverTime() {
        this.feverTimeActive = false;
        this.feverTimeEndAt = null;
        // 피버타임 종료시에도 모든 플레이어의 피버타임용 아이템 카운트 초기화
        players.forEach(Player::resetFeverItemCount);
    }

    // 보스 피격
    public void applyDamage(int damage) {
        this.bossHealth = Math.max(0, this.bossHealth - damage);
    }

    // 게임 종료 확인
    public boolean isGameFinished() {
        if (bossHealth <= 0) return true;
        if (gameStartTime == null) return false;
        // 현재 시간에서 게임 시작 시간을 뺀 값(경과 시간)이 보스 레벨의 제한 시간을 초과했는지 확인
        return (System.currentTimeMillis() - gameStartTime) >= (bossLevel.getTimeLimit() * 1000);
    }

}