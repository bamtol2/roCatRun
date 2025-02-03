package com.ssafy.roCatRun.domain.game.entity.game;


public enum GameStatus {
    WAITING,    // 대기중
    READY,      // 게임 시작 준비
    PLAYING,    // 게임 진행중
    FINISHED    // 게임 종료
}