package com.ssafy.roCatRun.domain.game.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * UserSession.java
 * 웹소켓으로 연결된 유저의 세션 정보를 담는 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class UserSession {
    private String userId; // 유저 식별자
    private String socketId; // 웹소켓 연결 식별자(=세션 식별)
    private long connectedAt; // 연결 시작 시간
}
