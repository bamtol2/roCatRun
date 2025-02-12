package com.ssafy.roCatRun.domain.stats.repository;

import com.ssafy.roCatRun.domain.stats.entity.GameStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameStatsRepository extends MongoRepository<GameStats, String> {
    List<GameStats> findByUserIdAndDateBetween(String userId, LocalDateTime start, LocalDateTime end);
    List<GameStats> findByUserId(String userId);
}