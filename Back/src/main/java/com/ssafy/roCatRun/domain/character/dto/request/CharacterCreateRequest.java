package com.ssafy.roCatRun.domain.character.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CharacterCreateRequest {
    @Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
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