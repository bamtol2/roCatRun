package com.ssafy.roCatRun.domain.boss.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class Boss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private BossDifficulty difficulty; // 난이도
    private int timeLimit; // 제한시간
    private int hpPerKm; // 1km당 HP
    private String distance; // 클리어하기 위해 달려야하는 거리(m)
    private String bossImage; // 보스이미지Url
    private String bossName; // 보스이름
    private int expRewardMin; // 최소 경험치 보상
    private int expRewardMax; // 최대 경험치 보상
    private String feverCondition; // 피버조건
    private int coinRewardMin; // 최소 코인 보상
    private int coinRewardMax; // 최대 코인 보상

}
