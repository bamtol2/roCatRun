package com.ssafy.roCatRun.domain.game.service.manager;

import com.ssafy.roCatRun.domain.game.entity.raid.BossLevel;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.GameStatus;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GameRoomManager.java
 * 게임방 관리 클래스(방 생성/삭제/조회 등 관리)
 */
@Component
@Slf4j
public class GameRoomManager {
    // 게임방 ID를 키로 하는 게임방 맵
    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    /**
     * 새로운 게임방 추가
     */
    public void addRoom(GameRoom room) {
        rooms.put(room.getId(), room);
        log.info("[Room Created] ID: {}, Boss Level: {}, Max Players: {}, Type: {}",
                room.getId(),
                room.getBossLevel(),
                room.getMaxPlayers(),
                room.isRandomMatch() ? "Random" : "Private");
    }

    /**
     * 게임방 ID로 조회
     */
    public Optional<GameRoom> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    /**
     * 게임방 정보 업데이트
     */
    public void updateRoom(GameRoom room) {
        rooms.put(room.getId(), room);
        log.debug("[Room Updated] ID: {}, Status: {}, Players: {}, Boss Health: {}",
                room.getId(),
                room.getStatus(),
                room.getPlayers().size(),
                room.getBossHealth());
    }

    /**
     * 게임방 삭제
     */
    public void removeRoom(String roomId) {
        GameRoom room = rooms.remove(roomId);
        if (room != null) {
            log.info("[Room Removed] ID: {}, Final Status: {}, Player Count: {}",
                    roomId, room.getStatus(), room.getPlayers().size());
        }
    }

    /**
     * 랜덤 매칭이 가능한 게임방 찾기
     * @param bossLevel 보스 난이도
     * @param maxPlayers 최대 인원
     * @return 조건에 맞는 게임방 (없으면 Optional.empty)
     */
    public Optional<GameRoom> findRandomRoom(BossLevel bossLevel, int maxPlayers) {
        for (GameRoom room : rooms.values()) {
            if (room.isRandomMatch() &&
                    room.getBossLevel() == bossLevel &&
                    room.getMaxPlayers() == maxPlayers &&
                    room.getStatus() == GameStatus.WAITING &&
                    room.getPlayers().size() < room.getMaxPlayers()) {
                return Optional.of(room);
            }
        }
        return Optional.empty();
    }

    /**
     * 유저 ID로 게임방 찾기
     */
    public Optional<GameRoom> findRoomByUserId(String userId) {
        for (GameRoom room : rooms.values()) {
            for (Player player : room.getPlayers()) {
                if (player.getId().equals(userId)) {
                    return Optional.of(room);
                }
            }
        }
        return Optional.empty();
    }


}