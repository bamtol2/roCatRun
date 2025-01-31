package com.ssafy.raidtest.raid.repository;

import com.ssafy.raidtest.raid.domain.statistics.GameResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticsRepository extends MongoRepository<GameResult, String> {
    @Query("{'playerIds': ?0, 'playedAt': {$gte: ?1}}")
    List<GameResult> findByUserIdAndPeriod(String userId, LocalDateTime startDate);

    @Query(value = "{'playerIds': ?0}", sort = "{'playedAt': -1}")
    List<GameResult> findRecentGamesByUserId(String userId, Pageable pageable);
}
