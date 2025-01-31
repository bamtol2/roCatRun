package com.ssafy.raidtest.raid.domain.game;

public enum GameStatus {
    INITIALIZING, // 초기화 중
    COUNTDOWN, // 대기(카운트다운)
    STARTED, // 게임 시작
    FEVER_READY, // 피버 준비 완료
    FEVER, // 피버타임
    ENDED // 게임 종료
}
