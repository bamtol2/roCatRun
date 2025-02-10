package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//
@Getter
@AllArgsConstructor
public class FeverTimeStartedResponse {
    private boolean active; // 피버 활성 여부
    private int duration; // 피버 지속 시간
}
