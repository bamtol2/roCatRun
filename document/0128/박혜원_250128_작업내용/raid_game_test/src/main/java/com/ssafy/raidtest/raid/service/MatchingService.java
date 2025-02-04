package com.ssafy.raidtest.raid.service;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import com.ssafy.raidtest.raid.domain.room.RaidRoom;
import com.ssafy.raidtest.raid.domain.room.RoomStatus;
import com.ssafy.raidtest.raid.exception.RoomFullException;
import com.ssafy.raidtest.raid.exception.RoomNotAvailableException;
import com.ssafy.raidtest.raid.exception.RoomNotFoundException;
import com.ssafy.raidtest.raid.memory.RoomMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {
    private final RoomMemoryStore roomStore;
    private final GameService gameService;

    // 방 생성
    public RaidRoom createRoom(String userId, Boss boss, int maxPlayers) {
        validateParameters(boss, maxPlayers);

        RaidRoom room = new RaidRoom();
        room.setRoomId(UUID.randomUUID().toString());
        room.setInviteCode(generateInviteCode());
        room.setBoss(boss);
        room.setMaxPlayers(maxPlayers);
        room.setStatus(RoomStatus.WAITING);
        room.setCreatedAt(LocalDateTime.now());

        room.getPlayerIds().add(userId);
        roomStore.saveRoom(room);

        return room;
    }

    // 초대 코드로 참여
    public RaidRoom joinByInviteCode(String userId, String inviteCode) {
        RaidRoom room = roomStore.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RoomNotFoundException("잘못된 초대코드 입니다."));

        return joinRoom(room.getRoomId(), userId);
    }

    // 방 참여
    public RaidRoom joinRoom(String roomId, String userId) {
        RaidRoom room = roomStore.getRoom(roomId)
                .orElseThrow(() -> new RoomNotFoundException(roomId));

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new RoomNotAvailableException("방 준비 중 입니다.");
        }

        if (room.getPlayerIds().size() >= room.getMaxPlayers()) {
            throw new RoomFullException("방이 가득찼습니다.");
        }

        room.getPlayerIds().add(userId);
        roomStore.saveRoom(room);

        // 인원이 다 차면 게임 시작
        if (room.getPlayerIds().size() == room.getMaxPlayers()) {
            gameService.startGame(roomId);
        }

        return room;
    }

    private void validateParameters(Boss boss, int maxPlayers) {
        if (boss == null) {
            throw new IllegalArgumentException("보스 정보가 존재하지 않습니다.");
        }
        if (maxPlayers < 1 || maxPlayers > 4) {
            throw new IllegalArgumentException("플레이어는 1~4명 사이입니다.");
        }
    }

    private String generateInviteCode() {
        return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }

    // 랜덤 매칭
    public RaidRoom matchPlayer(String userId, Boss boss, int maxPlayers) {
        validateParameters(boss, maxPlayers);

        // 조건에 맞는 대기방 검색
        Optional<RaidRoom> availableRoom = roomStore.findAvailableRoom(boss, maxPlayers);

        if (availableRoom.isPresent()) {
            RaidRoom room = availableRoom.get();
            return joinRoom(room.getRoomId(), userId);
        }

        // 방이 없으면 새로 생성
        log.info("매칭 가능한 방이 없어 새로운 방을 생성합니다. userId: {}", userId);
        return createRoom(userId, boss, maxPlayers);
    }
}