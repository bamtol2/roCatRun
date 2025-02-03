package com.ssafy.roCatRun.domain.game.entity.game;

public enum BossLevel {
    EASY(1000),
    NORMAL(2000),
    HARD(3000);

    private final int health;

    BossLevel(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
}
