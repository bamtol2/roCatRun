package com.ssafy.roCatRun.domain.gameCharacter.repository;

import com.ssafy.roCatRun.domain.gameCharacter.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Level 엔티티에 대한 데이터 접근을 관리하는 Repository
 */
@Repository
public interface LevelRepository extends JpaRepository<Level, Integer> {
    // 특정 레벨의 정보를 조회
    Level findByLevel(Integer level);
}
