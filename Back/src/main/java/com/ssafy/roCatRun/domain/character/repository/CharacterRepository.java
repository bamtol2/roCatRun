package com.ssafy.roCatRun.domain.character.repository;

import com.ssafy.roCatRun.domain.character.entity.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 캐릭터 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리
 */
public interface CharacterRepository extends JpaRepository<Character, Long> {
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
     * 레벨과 경험치를 기준으로 정렬된 캐릭터 목록을 조회합니다.
     * @param pageable 페이지네이션 정보
     * @return 정렬된 캐릭터 목록
     */
    @Query("SELECT c FROM Character c ORDER BY c.level DESC, c.experience DESC")
    Page<Character> findAllOrderByLevelAndExperience(Pageable pageable);

    /**
     * 특정 캐릭터의 랭킹을 계산합니다.
     * @param level 캐릭터 레벨
     * @param experience 캐릭터 경험치
     * @return 해당 캐릭터의 랭킹
     */
    @Query("SELECT COUNT(c) + 1 FROM Character c WHERE c.level > :level OR (c.level = :level AND c.experience > :experience)")
    Long findRankByLevelAndExperience(@Param("level") Integer level, @Param("experience") Integer experience);
}
