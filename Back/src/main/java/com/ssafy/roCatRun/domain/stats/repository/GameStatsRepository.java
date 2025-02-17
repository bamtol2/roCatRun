package com.ssafy.roCatRun.domain.stats.repository;

import com.ssafy.roCatRun.domain.stats.entity.GameStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameStatsRepository extends MongoRepository<GameStats, String> {
    // 기간 조회 시 날짜 내림차순 정렬
    List<GameStats> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDateTime start, LocalDateTime end);

    // 전체 조회 시 날짜 내림차순 정렬
    List<GameStats> findByUserIdOrderByDateDesc(String userId);
}