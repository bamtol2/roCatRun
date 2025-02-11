package com.ssafy.roCatRun.domain.game.entity.raid;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
@Getter
@Setter
@NoArgsConstructor
public class GameResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private GameCharacter character;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BossLevel bossLevel;

    @Column(nullable = false)
    private boolean isCleared;

    private Duration runningTime;
    private double totalDistance;
    private double paceAvg;
    private double heartRateAvg;
    private double cadenceAvg;
    private int itemUseCount;
    private int rewardExp;
    private int rewardCoin;

    @Column(nullable = false)
    private LocalDateTime playedAt;

    @PrePersist
    protected void onCreate() {
        playedAt = LocalDateTime.now();
    }

    @Builder
    public GameResult(GameCharacter character, BossLevel bossLevel, boolean isCleared,
                      Duration runningTime, double totalDistance, double paceAvg,
                      double heartRateAvg, double cadenceAvg, int itemUseCount,
                      int rewardExp, int rewardCoin) {
        this.character = character;
        this.bossLevel = bossLevel;
        this.isCleared = isCleared;
        this.runningTime = runningTime;
        this.totalDistance = totalDistance;
        this.paceAvg = paceAvg;
        this.heartRateAvg = heartRateAvg;
        this.cadenceAvg = cadenceAvg;
        this.itemUseCount = itemUseCount;
        this.rewardExp = rewardExp;
        this.rewardCoin = rewardCoin;
    }
}
