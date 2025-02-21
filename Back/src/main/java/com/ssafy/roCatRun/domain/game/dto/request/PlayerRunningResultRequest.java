package com.ssafy.roCatRun.domain.game.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor // 전체 필드 생성자 유지
public class PlayerRunningResultRequest {
    private long runningTimeMillis; // 초 단위 저장
    private double totalDistance;
    private double paceAvg;
    private double heartRateAvg;
    private double cadenceAvg;

    public long getRunningTimeSec() {
        return runningTimeMillis / 1000;
    }
}
