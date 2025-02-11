package com.ssafy.roCatRun.domain.stats.service;

import com.ssafy.roCatRun.domain.game.dto.request.PlayerRunningResultRequest;
import com.ssafy.roCatRun.domain.game.entity.raid.GameRoom;
import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import com.ssafy.roCatRun.domain.game.service.GameService;
import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import com.ssafy.roCatRun.domain.gameCharacter.repository.GameCharacterRepository;
import com.ssafy.roCatRun.domain.stats.dto.response.DailyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.MonthlyStatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.StatsResponse;
import com.ssafy.roCatRun.domain.stats.dto.response.WeeklyStatsResponse;
import com.ssafy.roCatRun.domain.stats.entity.GameStats;
import com.ssafy.roCatRun.domain.stats.repository.GameStatsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.catalina.manager.StatusTransformer.formatTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameStatsService {
    private final GameStatsRepository gameStatsRepository;
    private final GameCharacterRepository characterRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public void saveGameStats(GameRoom room, Map<String, PlayerRunningResultRequest> results,
                              Map<String, GameService.GameResultInfo> rewardInfo) {
        // 1. 플레이어들을 순위별로 정렬
        List<Player> sortedPlayers = sortPlayersByRank(room);

        // 2. 각 플레이어별로 통계 저장
        for (Player currentPlayer : sortedPlayers) {
            // 플레이어들 정보 생성
            List<GameStats.PlayerStats> playerStats = new ArrayList<>();
            for (Player player : sortedPlayers) {
                PlayerRunningResultRequest result = results.get(player.getId());

                GameStats.PlayerStats playerStat = GameStats.PlayerStats.builder()
                        .rank(getRank(sortedPlayers, player.getId()) + 1)
                        .profileUrl(player.getCharacterImage())
                        .nickname(player.getNickname())
                        .distance(result.getTotalDistance())
                        .attackCount(player.getUsedItemCount())
                        .build();

                playerStats.add(playerStat);
            }

            // 3. 현재 플레이어의 게임 상세 정보 생성
            PlayerRunningResultRequest currentPlayerResult = results.get(currentPlayer.getId()); // 러닝 상세 정보
            GameService.GameResultInfo currentPlayerReward = rewardInfo.get(currentPlayer.getId()); // 보상 정보

            GameStats.GameDetails details = GameStats.GameDetails.builder()
                    .pace(currentPlayerResult.getPaceAvg())
                    .calories(currentPlayerReward.getCalories())
                    .cadence(currentPlayerResult.getCadenceAvg())
                    .distance(currentPlayerResult.getTotalDistance())
                    .runningTime(currentPlayerResult.getRunningTimeSec())
                    .build();

            // 4. 현재 플레이어의 게임 통계 저장
            GameStats gameStats = GameStats.builder()
                    .userId(currentPlayer.getId())
                    .roomId(room.getId())
                    .date(LocalDateTime.now())
                    .difficulty(room.getBossLevel())
                    .result(room.getBossHealth() <= 0) // 보스 HP가 0 이하면 클리어
                    .players(playerStats)
                    .details(details)
                    .build();

            gameStatsRepository.save(gameStats);
        }
    }

    // 플레이어 순위별 정렬
    private List<Player> sortPlayersByRank(GameRoom room) {
        return room.getPlayers().stream()
                .sorted((p1, p2) -> {
                    if (p1.getUsedItemCount() != p2.getUsedItemCount()) {
                        return p2.getUsedItemCount() - p1.getUsedItemCount();
                    }
                    return Double.compare(
                            p2.getRunningData().getDistance(),
                            p1.getRunningData().getDistance()
                    );
                })
                .collect(Collectors.toList());
    }

    // 특정 플레이어의 순위 계산
    private int getRank(List<Player> sortedPlayers, String userId) {
        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getId().equals(userId)) {
                return i;
            }
        }
        return sortedPlayers.size() - 1;
    }

    // 일별 통계 조회
    public DailyStatsResponse getDailyStats(String userId, LocalDate date) {
        // 해당 날짜의 시작과 끝 시간 설정
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // 해당 기간의 게임 기록 조회
        List<GameStats> dailyGames = gameStatsRepository.findByUserIdAndDateBetween(
                userId, startOfDay, endOfDay);
        // 적절한 응답으로 포맷팅
        return buildDailyStatsResponse(userId, dailyGames);
    }

    private DailyStatsResponse buildDailyStatsResponse(String userId, List<GameStats> games) {
        List<DailyStatsResponse.Game> gameList = new ArrayList<>();

        // 각 게임 기록을 응답 형식으로 변환
        for (GameStats game : games) {
            // 플레이어 정보 변환
            List<DailyStatsResponse.Player> players = new ArrayList<>();
            for (GameStats.PlayerStats playerStat : game.getPlayers()) {
                DailyStatsResponse.Player player = DailyStatsResponse.Player.builder()
                        .rank(playerStat.getRank())
                        .profileUrl(playerStat.getProfileUrl())
                        .nickname(playerStat.getNickname())
                        .distance(playerStat.getDistance())
                        .attackCount(playerStat.getAttackCount())
                        .build();
                players.add(player);
            }

            // 게임 상세 정보 변환
            DailyStatsResponse.GameDetails details = DailyStatsResponse.GameDetails.builder()
                    .pace(formatPace(game.getDetails().getPace()))
                    .calories(game.getDetails().getCalories())
                    .cadence(game.getDetails().getCadence())
                    .distance(game.getDetails().getDistance())
                    .runningTime(formatTime(game.getDetails().getRunningTime()))
                    .build();

            // 게임 정보 생성
            DailyStatsResponse.Game gameInfo = DailyStatsResponse.Game.builder()
                    .roomId(game.getRoomId())
                    .date(game.getDate().toString())
                    .difficulty(game.getDifficulty().toString())
                    .result(game.isResult())
                    .players(players)
                    .details(details)
                    .build();

            gameList.add(gameInfo);
        }

        return DailyStatsResponse.builder()
                .userId(userId)
                .games(gameList)
                .build();
    }

    // 주별 통계 조회
    public WeeklyStatsResponse getWeeklyStats(String userId, LocalDate date) {
        // 해당 주의 시작(월요일)과 끝(일요일) 날짜 설정
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        // 해당 기간의 게임 기록 조회
        List<GameStats> weeklyGames = gameStatsRepository.findByUserIdAndDateBetween(
                userId,
                startOfWeek.atStartOfDay(),
                endOfWeek.plusDays(1).atStartOfDay()  // 다음날 0시 이전까지
        );

        return buildWeeklyStatsResponse(userId, weeklyGames, startOfWeek, endOfWeek);
    }

    // 월별 통계 조회
    public MonthlyStatsResponse getMonthlyStats(String userId, YearMonth yearMonth) {
        // 해당 월의 시작일과 마지막일 구하기
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        // 해당 기간의 게임 기록 조회
        List<GameStats> monthlyGames = gameStatsRepository.findByUserIdAndDateBetween(
                userId,
                startOfMonth.atStartOfDay(),
                endOfMonth.plusDays(1).atStartOfDay()  // 다음날 0시 이전까지
        );

        // 조회된 데이터 로깅
        log.debug("Found {} games for user {} in {}",
                monthlyGames.size(), userId, yearMonth);
        monthlyGames.forEach(game ->
                log.debug("Game date: {}, distance: {}",
                        game.getDate(), game.getDetails().getDistance())
        );

        return buildMonthlyStatsResponse(userId, monthlyGames, startOfMonth, endOfMonth);
    }

    private WeeklyStatsResponse buildWeeklyStatsResponse(String userId, List<GameStats> games,
                                                         LocalDate startDate, LocalDate endDate) {
        // 1. 해당 주의 전체 통계 계산
        double totalDistance = 0; // 총 러닝 거리
        long totalTime = 0; // 총 러닝 시간
        double totalPace = 0; // 총 페이스
        int validGameCount = 0; // 게임 횟수

        // 해당 기간의 게임만 계산
        for (GameStats game : games) {
            LocalDate gameDate = game.getDate().toLocalDate();
            // startDate부터 endDate까지의 데이터만 처리
            if (!gameDate.isBefore(startDate) && !gameDate.isAfter(endDate)) {
                GameStats.GameDetails details = game.getDetails();
                if (details != null) {
                    totalDistance += details.getDistance();
                    totalTime += details.getRunningTime();
                    totalPace += details.getPace();
                    validGameCount++;
                }
            }
        }

        // 2. 일별 통계 생성 (월요일부터 일요일까지)
        List<WeeklyStatsResponse.WeeklyDailyStat> dailyStats = new ArrayList<>();
        LocalDate currentDate = startDate;  // 월요일부터 시작

        while (!currentDate.isAfter(endDate)) {  // 일요일까지
            final LocalDate date = currentDate;
            double dailyDistance = 0;

            // 해당 날짜의 총 거리 계산
            for (GameStats game : games) {
                if (game.getDate().toLocalDate().equals(date)) {
                    dailyDistance += game.getDetails().getDistance();
                }
            }

            // 날짜 형식: "2025-02-10" 형태로 저장
            dailyStats.add(new WeeklyStatsResponse.WeeklyDailyStat(
                    date.toString(),
                    dailyDistance
            ));

            currentDate = currentDate.plusDays(1);
        }

        // 3. 평균 페이스 계산
        double averagePace = validGameCount > 0 ? totalPace / validGameCount : 0;

        // 4. 응답 생성
        WeeklyStatsResponse.Summary summary = WeeklyStatsResponse.Summary.builder()
                .totalDistance(totalDistance)
                .totalRun(validGameCount)  // 유효한 게임 수로 변경
                .averagePace(formatPace(averagePace))
                .totalTime(formatTime(totalTime))
                .build();

        WeeklyStatsResponse.StatsData data = WeeklyStatsResponse.StatsData.builder()
                .summary(summary)
                .dailyStats(dailyStats)
                .build();

        return WeeklyStatsResponse.builder()
                .userId(userId)
                .data(data)
                .build();
    }

    private MonthlyStatsResponse buildMonthlyStatsResponse(String userId, List<GameStats> games,
                                                           LocalDate startDate, LocalDate endDate) {
        // 1. 해당 월의 전체 통계 계산
        double totalDistance = 0;
        long totalTime = 0;
        double totalPace = 0;
        int validGameCount = 0;

        // 해당 기간의 게임만 계산
        for (GameStats game : games) {
            LocalDate gameDate = game.getDate().toLocalDate();
            // startDate부터 endDate까지의 데이터만 처리
            if (!gameDate.isBefore(startDate) && !gameDate.isAfter(endDate)) {
                GameStats.GameDetails details = game.getDetails();
                if (details != null) {
                    totalDistance += details.getDistance();
                    totalTime += details.getRunningTime();
                    totalPace += details.getPace();
                    validGameCount++;
                }
            }
        }

        // 2. 일별 통계 생성
        List<MonthlyStatsResponse.MonthlyDailyStat> dailyStats = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            final LocalDate date = currentDate;
            double dailyDistance = 0;

            // 해당 날짜의 총 거리 계산
            for (GameStats game : games) {
                if (game.getDate().toLocalDate().equals(date)) {
                    // 로그 추가하여 데이터 확인
                    log.debug("Date: {}, Adding distance: {}", date, game.getDetails().getDistance());
                    dailyDistance += game.getDetails().getDistance();
                }
            }

            // 로그 추가하여 최종 집계 확인
            log.debug("Final daily distance for {}: {}", date, dailyDistance);

            dailyStats.add(new MonthlyStatsResponse.MonthlyDailyStat(
                    date.toString(),
                    dailyDistance
            ));

            currentDate = currentDate.plusDays(1);
        }

        // 3. 평균 페이스 계산
        double averagePace = validGameCount > 0 ? totalPace / validGameCount : 0;

        // 4. 응답 생성
        MonthlyStatsResponse.Summary summary = MonthlyStatsResponse.Summary.builder()
                .totalDistance(totalDistance)
                .totalRun(validGameCount)
                .averagePace(formatPace(averagePace))
                .totalTime(formatTime(totalTime))
                .build();

        MonthlyStatsResponse.StatsData data = MonthlyStatsResponse.StatsData.builder()
                .summary(summary)
                .dailyStats(dailyStats)
                .build();

        return MonthlyStatsResponse.builder()
                .userId(userId)
                .data(data)
                .build();
    }

    // 초를 "HH:mm:ss" 형식으로 포맷팅
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    // "HH:mm:ss" 형식의 시간을 초로 변환
    private long parseTimeToSeconds(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * 3600 + minutes * 60 + seconds;
    }

    // 페이스 포맷팅 (5.41 -> "5'41"" 형식으로)
    private String formatPace(double pace) {
        int minutes = (int) pace;  // 정수 부분이 분
        int seconds = (int) ((pace - minutes) * 100);  // 소수 부분 * 100이 초
        return String.format("%d'%02d\"", minutes, seconds);
    }
}