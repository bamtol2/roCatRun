package com.ssafy.roCatRun.domain.gameCharacter.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 레벨 시스템을 관리하는 엔티티
 * 각 레벨별 필요 경험치를 저장하고 관리
 */
@Entity
@Table(name = "levels")
@Getter
@NoArgsConstructor
public class Level {
    @Id
    @Column(name = "level")
    private Integer level;

    @Column(name = "required_exp", nullable = false)
    private Integer requiredExp;

    // 레벨 생성 팩토리 메서드
    public static Level createLevel(Integer level, Integer requiredExp) {
        Level levelEntity = new Level();
        levelEntity.level = level;
        levelEntity.requiredExp = requiredExp;
        return levelEntity;
    }

    // 다음 레벨까지 남은 경험치 계산
    public Integer calculateRemainingExp(Integer currentExp) {
        return this.requiredExp - currentExp;
    }
}
