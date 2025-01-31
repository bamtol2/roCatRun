package com.ssafy.raidtest.raid.domain.game;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class RaidGame {
    private String gameId;
    private String roomId;
    private Boss boss; // 보스 정보
    private int currentBossHp; // 현재 보스 체력
    private Map<String, PlayerGameStatus> playerStatuses = new ConcurrentHashMap<>(); // 게임 중인 유저의 상태
    private boolean isFeverMode; // 피버여부
    private int feverItemCount; // 피버에 들어가기 위한 아이템 사용 수
    private GameStatus status; // 게임 상태..?
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
