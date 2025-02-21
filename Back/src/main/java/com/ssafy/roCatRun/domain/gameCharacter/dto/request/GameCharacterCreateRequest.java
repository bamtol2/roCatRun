package com.ssafy.roCatRun.domain.gameCharacter.dto.request;

import com.ssafy.roCatRun.global.validation.annotation.ValidNickname;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GameCharacterCreateRequest {
    @ValidNickname
    private String nickname;

    @NotNull(message = "키를 입력해주세요.")
    private Integer height;

    @NotNull(message = "몸무게를 입력해주세요.")
    private Integer weight;

    @NotNull(message = "나이를 입력해주세요.")
    private Integer age;

    @NotNull(message = "성별을 입력해주세요.")
    private String gender;
}