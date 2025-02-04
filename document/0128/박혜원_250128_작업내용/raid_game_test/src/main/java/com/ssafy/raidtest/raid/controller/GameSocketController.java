package com.ssafy.raidtest.raid.controller;

import com.ssafy.raidtest.raid.dto.request.DistanceUpdateRequest;
import com.ssafy.raidtest.raid.dto.request.ItemUseRequest;
import com.ssafy.raidtest.raid.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * WebSocket을 통한 실시간 게임 통신을 처리하는 컨트롤러
 * STOMP 프로토콜을 사용하여 클라이언트와 서버 간 양방향 통신
 */
@Controller
@RequiredArgsConstructor
public class GameSocketController {
    private final GameService gameService;

    /**
     * 유저의 실시간 러닝 정보 업데이트 처리
     * 클라이언트 구독 주소: /topic/game/{gameId}
     * 클라이언트 전송 주소: /app/game/{gameId}/distance
     *
     * @param gameId    게임 세션 ID
     * @param principal 현재 접속한 유저 정보
     * @param request   거리 업데이트 요청 정보
     */
    @MessageMapping("/game/{gameId}/distance")
    public void updateDistance(@DestinationVariable String gameId,
                               Principal principal,
                               @Payload DistanceUpdateRequest request) {
        gameService.updateDistance(gameId, principal.getName(), request.getDistance());
    }

    /**
     * 유저의 아이템 사용 처리
     * 클라이언트 구독 주소: /topic/game/{gameId}
     * 클라이언트 전송 주소: /app/game/{gameId}/item
     *
     * @param gameId    게임 세션 ID
     * @param principal 현재 접속한 유저 정보
     * @param request   아이템 사용 요청 정보
     */
    @MessageMapping("/game/{gameId}/item")
    public void useItem(@DestinationVariable String gameId,
                        Principal principal,
                        @Payload ItemUseRequest request) {
        gameService.useItem(gameId, principal.getName(), request.getItemId());
    }
}
