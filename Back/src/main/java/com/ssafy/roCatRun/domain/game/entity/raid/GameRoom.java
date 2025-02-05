package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GameRoom {
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
        this.bossHealth = bossLevel.getHealth();
        this.feverTimeActive = false;
    }

    public boolean addPlayer(Player player) {
        if (players.size() >= maxPlayers) {
            return false;
        }
        return players.add(player);
    }

    public boolean isGameReady() {
        return players.size() == maxPlayers;
    }

    public void removePlayer(String userId) {
        players.removeIf(player -> player.getId().equals(userId));
    }
}