package com.ssafy.roCatRun.domain.game.dto.request;

import com.ssafy.roCatRun.domain.game.entity.raid.RunningData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RunningDataUpdateRequest {
    private RunningData runningData;
}
