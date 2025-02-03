package com.ssafy.roCatRun.domain.game.service;

import com.ssafy.roCatRun.domain.game.dto.request.MatchRequest;
import com.ssafy.roCatRun.domain.game.entity.game.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.game.Player;
import com.ssafy.roCatRun.domain.game.entity.manager.GameRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * GameService.java
 * 게임 매칭 및 방 관리 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRoomManager gameRoomManager;

    /**
     * 랜덤 매칭 처리
     * 조건에 맞는 방이 있으면 입장시키고, 없으면 새로운 방 생성
     * @param userId 유저 식별자
     * @param request 매칭 요청 정보 (보스 레벨, 최대 인원 등)
     * @return 입장한 또는 생성된 게임방
     */
    public GameRoom findOrCreateRandomMatch(String userId, MatchRequest request) {
        // 1. 먼저 적합한 방이 있는지 찾기
        Optional<GameRoom> existingRoom = gameRoomManager.findRandomRoom(request.getBossLevel(), request.getMaxPlayers());

        // 기존 방이 있으면 해당 방에 입장
        if (existingRoom.isPresent()) {
            GameRoom room = existingRoom.get();
            Player player = new Player(userId);
            room.addPlayer(player);
            gameRoomManager.updateRoom(room);
            return room;
        }

        // 2. 없으면 새로운 방 생성
        GameRoom newRoom = new GameRoom(
                UUID.randomUUID().toString(),
                request.getBossLevel(),
                request.getMaxPlayers(),
                true
        );

        Player player = new Player(userId);
        newRoom.addPlayer(player);

        gameRoomManager.addRoom(newRoom);
        return newRoom;
    }

    /**
     * 유저 연결 종료 처리
     * 게임방에서 해당 유저를 제거하고, 필요시 방 삭제
     */
    public void handleUserDisconnect(String userId) {
        Optional<GameRoom> room = gameRoomManager.findRoomByUserId(userId);
        room.ifPresent(r -> {
            r.removePlayer(userId);
            if (r.getPlayers().isEmpty()) {
                gameRoomManager.removeRoom(r.getId());
            } else {
                gameRoomManager.updateRoom(r);
            }
        });
    }
}
