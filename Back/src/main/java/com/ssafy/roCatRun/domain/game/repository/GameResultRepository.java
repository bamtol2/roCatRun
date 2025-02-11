package com.ssafy.roCatRun.domain.game.repository;

import com.ssafy.roCatRun.domain.game.entity.raid.BossLevel;
import com.ssafy.roCatRun.domain.game.entity.raid.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    // 특정 캐릭터의 게임 결과 조회
    List<GameResult> findByCharacterIdOrderByPlayedAtDesc(Long characterId);

    // 특정 캐릭터의 클리어 횟수 조회
    @Query("SELECT COUNT(g) FROM GameResult g WHERE g.character.id = :characterId AND g.isCleared = true")
    long countClearedGamesByCharacterId(@Param("characterId") Long characterId);

    // 특정 난이도의 클리어 횟수 조회
    @Query("SELECT COUNT(g) FROM GameResult g WHERE g.character.id = :characterId AND g.bossLevel = :bossLevel AND g.isCleared = true")
    long countClearedGamesByCharacterIdAndBossLevel(@Param("characterId") Long characterId, @Param("bossLevel") BossLevel bossLevel);
}
