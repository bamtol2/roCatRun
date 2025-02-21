package com.ssafy.roCatRun.domain.myPage.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateRequest {
    private String nickname;    // 캐릭터 닉네임
    private Integer height;     // 키
    private Integer weight;     // 몸무게
    private Integer age;        // 나이
    private String gender;      // 성별
}

