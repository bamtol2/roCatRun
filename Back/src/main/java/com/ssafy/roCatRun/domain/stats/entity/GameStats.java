package com.ssafy.roCatRun.domain.stats.entity;

import com.ssafy.roCatRun.domain.game.entity.raid.BossLevel;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "gameStats")
@Data
@Builder
public class GameStats {
    @Id
    private String id; // 통계 ID
    private String userId; // 유저 ID
    private String roomId; // 방 ID
    private LocalDateTime date; // 게임 날짜
    private BossLevel difficulty; // 게임 보스 난이도
    private boolean result; // 게임 클리어 여부
    private List<PlayerStats> players; // 해당 게임의 유저 정보들
    private GameDetails details; // 게임 상세 정보

    @Data
    @Builder
    public static class PlayerStats {
        private int rank; // 해당 게임 내 순위
        private String profileUrl; // 유저 프로필
        private String nickname; // 유저 닉네임
        private String userId;
        private double distance; // 러닝 거리
        private int attackCount; // 아이템 사용 횟수
    }

    @Data
    @Builder
    public static class GameDetails {
        private double pace; // 평균 페이스
        private int calories; // 소모 칼로리
        private double cadence; // 평균 케이던스
        private double distance; // 총 러닝 거리
        private long runningTime; // 총 러닝 시간(초)
    }


}