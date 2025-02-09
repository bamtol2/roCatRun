package com.ssafy.roCatRun.domain.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@AllArgsConstructor
public class PlayerRunningResultRequest {
    private String userId;
    private Duration runningTime;
    private double totalDistance;
    private double paceAvg;
    private double heartRateAvg;
    private double cadenceAvg;
}
