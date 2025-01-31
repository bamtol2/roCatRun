package com.ssafy.raidtest.raid.domain.boss;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Boss {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int difficulty;// 1: Easy, 2: Normal, 3: Hard
    private int maxHp;// 보스 체력
    private int timeLimit;// 제한시간 (초)
}
