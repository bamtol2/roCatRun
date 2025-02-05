package com.ssafy.roCatRun.domain.game.dto.request;

import com.ssafy.roCatRun.domain.game.entity.raid.RunningData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RunningDataUpdateRequest {
    private RunningData runningData;
}
