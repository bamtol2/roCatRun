package com.ssafy.raidtest.raid.domain.room;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class RaidRoom {
    private String roomId; // 방 아이디
    private String inviteCode; // 초대 코드
    private Boss boss; // 보스 정보
    private int maxPlayers; // 최대 플레이어
    private Set<String> playerIds = new ConcurrentHashSet<>();
    private RoomStatus status; // 방 상태
    private LocalDateTime createdAt;
}
