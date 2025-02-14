package com.ssafy.roCatRun.domain.gameCharacter.repository;

import com.ssafy.roCatRun.domain.gameCharacter.entity.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * 캐릭터 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리
 */
public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
    int MAX_RANKING_SIZE = 30;

    /**
     * 닉네임 존재 여부를 확인합니다.
     * @param nickname 검사할 닉네임
     * @return 닉네임 존재 여부
     */
    boolean existsByNickname(String nickname);

    /**
     * 회원 ID로 캐릭터를 삭제합니다.
     * @param memberId 회원 ID
     */
    void deleteByMember_Id(Long memberId);

    /**
     * 특정 캐릭터를 제외한 상위 N개의 캐릭터를 레벨과 경험치 순으로 조회합니다.
     * @param characterId 제외할 캐릭터 ID
     * @param limit 조회할 랭킹 개수
     * @return 상위 N개의 캐릭터 목록
     */
    @Query("SELECT c FROM GameCharacter c WHERE c.id != :characterId " +
            "ORDER BY c.levelInfo.level DESC, c.experience DESC")
    List<GameCharacter> findTopNByIdNotOrderByLevelDescExperienceDesc(
            @Param("characterId") Long characterId,
            @Param("limit") int limit
    );

    /**
     * 특정 캐릭터의 랭킹을 계산합니다.
     * 해당 캐릭터보다 레벨이 높거나, 같은 레벨에서 경험치가 높은 캐릭터의 수를 계산하여 순위 결정
     * @param level 캐릭터 레벨
     * @param experience 캐릭터 경험치
     * @return 해당 캐릭터의 랭킹
     */
    @Query("SELECT COUNT(gc) + 1 FROM GameCharacter gc " +
            "WHERE gc.levelInfo.level > :level " +
            "OR (gc.levelInfo.level = :level AND gc.experience > :experience)")
    Long findRankByLevelAndExperience(
            @Param("level") Integer level,
            @Param("experience") Integer experience
    );
}