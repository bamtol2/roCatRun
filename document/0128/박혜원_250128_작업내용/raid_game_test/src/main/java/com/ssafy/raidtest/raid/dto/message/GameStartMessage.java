package com.ssafy.raidtest.raid.dto.message;

import com.ssafy.raidtest.raid.domain.boss.Boss;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GameStartMessage {
    private String gameId; // 게임 세션 ID
    private Boss boss; // 도전할 보스 정보
    private List<String> players; // 참여 플레이어 목록
    private LocalDateTime startTime; // 시작 시간
    private LocalDateTime endTime; // 제한 시간

}
