package com.ssafy.raidtest.raid.domain.statistics;

import com.ssafy.raidtest.raid.dto.PlayerStats;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "game_results")
@Getter
@Setter
@Builder
public class GameResult {
    @Id
    private String id; // 게임 결과 이이디
    private String gameId; // 게임 아이디
    private List<String> playerIds; // 게임에 참여한 플레이어의 아이디
    private Long bossId; // 보스 아이디
    private String bossName;
    private boolean victory; // 승리 여부
    private String mvpUserId; // mvp 유저의 아이디
    private int clearTime; // 클리어 타임
    private LocalDateTime playedAt; // 플레이 시간
    @Indexed // MongoDB 인덱스 - 날짜 기반 쿼리 성능 향상
    private LocalDate playDate; // 게임 진행 날짜
    private Map<String, PlayerStats> playerStats;
}
