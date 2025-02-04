package com.ssafy.roCatRun.domain.game.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.JsonObject;
import com.ssafy.roCatRun.domain.game.dto.request.CreateRoomRequest;
import com.ssafy.roCatRun.domain.game.dto.request.MatchRequest;
import com.ssafy.roCatRun.domain.game.dto.response.GameCountdownResponse;
import com.ssafy.roCatRun.domain.game.dto.response.GameReadyResponse;
import com.ssafy.roCatRun.domain.game.dto.response.GameStartResponse;
import com.ssafy.roCatRun.domain.game.entity.game.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.game.GameStatus;
import com.ssafy.roCatRun.domain.game.entity.game.Player;
import com.ssafy.roCatRun.domain.game.entity.manager.GameRoomManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GameService.java
 * 게임 매칭 및 방 관리 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private static final int INVITE_CODE_LENGTH = 6;
    private final Map<String, String> inviteCodes = new ConcurrentHashMap<>(); // 초대코드 - 방코드 매칭
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SocketIOServer server;

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
            handlePlayerJoin(room, userId); // handlePlayerJoin 사용
            return room;
        }

        // 2. 없으면 새로운 방 생성
        GameRoom newRoom = new GameRoom(
                UUID.randomUUID().toString(),
                request.getBossLevel(),
                request.getMaxPlayers(),
                true
        );

        gameRoomManager.addRoom(newRoom);
        handlePlayerJoin(newRoom, userId); // handlePlayerJoin 사용
        return newRoom;
    }

    /**
     * 유저 연결 종료 처리
     * 게임방에서 해당 유저를 제거하고, 필요시 방 삭제
     * @param userId 유저 식별자
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

    /**
     * 방 생성
     * @param userId 유저 식별자
     * @param request 방 정보(보스 레벨, 참여 인원)
     * @return
     */
    public GameRoom createPrivateRoom(String userId, CreateRoomRequest request){
        // 새로운 방 생성
        GameRoom newRoom = new GameRoom(
                UUID.randomUUID().toString(),
                request.getBossLevel(),
                request.getMaxPlayers(),
                false
        );

        // 초대 코드 생성
        String inviteCode = generateInviteCode();
        inviteCodes.put(inviteCode, newRoom.getId());
        newRoom.setInviteCode(inviteCode);

        gameRoomManager.addRoom(newRoom);
        handlePlayerJoin(newRoom, userId); // handlePlayerJoin 사용
        return newRoom;
    }

    /**
     * 초대코드로 방 참여
     * @param userId 유저 식별자
     * @param inviteCode 초대코드
     * @return
     */
    public GameRoom joinRoomByInviteCode(String userId, String inviteCode){
        String roomId = inviteCodes.get(inviteCode);
        if(roomId==null){
            throw new IllegalArgumentException("Invalid invite code");
        }

        GameRoom room = gameRoomManager.getRoom(roomId)
                .orElseThrow(()->new IllegalArgumentException("Room not found"));

        handlePlayerJoin(room, userId);
        return room;
    }

    /**
     * 방에 유저 추가
     * @param room 방 정보
     * @param userId 유저 식별자
     */
    public void handlePlayerJoin(GameRoom room, String userId) {
        // 게임 중이면 입장 불가
        if (room.getStatus() != GameStatus.WAITING) {
            throw new IllegalStateException("Game is already in progress");
        }

        // 인원 초과 체크
        if (room.getPlayers().size() >= room.getMaxPlayers()) {
            throw new IllegalStateException("Room is full");
        }

        // 플레이어 추가
        Player player = new Player(userId);
        room.addPlayer(player);

        gameRoomManager.updateRoom(room);

    }

    /**
     * 게임 시작 조건 체크 및 시작
     */
    public void checkAndStartGame(GameRoom room) {
        // 최대 인원 도달 시 게임 시작 카운트다운 시작
        if (room.getPlayers().size() == room.getMaxPlayers()) {
            startGameCountdown(room);
        }
    }

    /**
     * 인원이 다 모였을 때 카운트다운 후 게임 시작
     * @param room 방 정보
     */
    public void startGameCountdown(GameRoom room){
        room.setStatus(GameStatus.READY);
        gameRoomManager.updateRoom(room);

        // 모든 플레이어에게 READY 상태 알림
        server.getRoomOperations(room.getId()).sendEvent("gameReady", new GameReadyResponse(
                "게임이 곧 시작됩니다!"
        ));

        // 3초 뒤 카운트다운
        AtomicInteger count = new AtomicInteger(3);
        ScheduledFuture<?> countdownTask = scheduler.scheduleAtFixedRate(()->{
            if(count.get()>0){
                //카운트다운 전송
                server.getRoomOperations(room.getId()).sendEvent("countdown",
                        new GameCountdownResponse(count.get(), room.getId()));
                count.decrementAndGet();
            }else{
                // 게임시작
                room.setStatus(GameStatus.PLAYING);
                gameRoomManager.updateRoom(room);
                server.getRoomOperations(room.getId()).sendEvent("gameStart", new GameStartResponse(
                        room.getId(),
                        "게임이 시작되었습니다!"
                ));

                // 카운트다운 테스크 종료
                throw new RuntimeException("Countdown completed");
            }
        }, 0, 1, TimeUnit.SECONDS);

        // 에러 핸들링(카운트다운 완료 시 태스크 종료)
        scheduler.schedule(()->countdownTask.cancel(true), 4, TimeUnit.SECONDS);
    }


    /**
     * 초대코드 생성
     */
    private String generateInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code;
        do{
            code = new StringBuilder();
            for(int i=0; i<INVITE_CODE_LENGTH; i++){
                code.append(chars.charAt(new Random().nextInt(chars.length())));
            }
        }while(inviteCodes.containsKey(code.toString())); // 중복체크

        return code.toString();
    }
}
