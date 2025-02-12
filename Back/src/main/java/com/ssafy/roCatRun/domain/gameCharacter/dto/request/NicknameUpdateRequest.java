package com.ssafy.roCatRun.domain.gameCharacter.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

// 닉네임 수정 요청 DTO
@Getter                 // getter 메서드를 자동으로 생성
@NoArgsConstructor      // 기본 생성자 자동 생성
public class NicknameUpdateRequest {
    private String newNickname;
}
