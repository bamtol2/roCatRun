package com.ssafy.roCatRun.domain.character.repository;

import com.ssafy.roCatRun.domain.character.entity.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    boolean existsByNickname(String nickname);
    void deleteByMember_Id(Long memberId);
}