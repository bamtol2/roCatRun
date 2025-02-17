package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.Getter;

@Getter
public enum BossLevel {
//    EASY(1000, 1800, 300, 100),    // 1km당 1000HP, 30분
    EASY(250, 300, 300, 100),    // 1km당 1000HP, 30분
//    NORMAL(1250, 1800, 500, 200),  // 1km당 1000HP, 30분
    NORMAL(1250, 60, 500, 200),  // 1km당 1000HP, 30분
    HARD(1500, 1800, 800, 350);    // 1km당 1000HP, 30분

    private final int hpPerKm;   // 1km당 HP
    private final int timeLimit; // 제한시간(초)
    private final int baseExp;   // 기본 경험치
    private final int baseCoin;  // 기본 코인

    BossLevel(int hpPerKm, int timeLimit, int baseExp, int baseCoin) {
        this.hpPerKm = hpPerKm;
        this.timeLimit = timeLimit;
        this.baseExp = baseExp;
        this.baseCoin = baseCoin;
    }

    // 인원 수에 따른 보스 체력
    public int calculateInitialHp(int playerCount) {
        return hpPerKm * 4 * playerCount;  // 4km * 인원수 만큼의 HP
    }
}
