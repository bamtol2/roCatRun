package com.ssafy.roCatRun.domain.gameCharacter.dto.request;

import com.ssafy.roCatRun.global.validation.annotation.ValidNickname;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameUpdateRequest {
    @ValidNickname
    private String newNickname;
}