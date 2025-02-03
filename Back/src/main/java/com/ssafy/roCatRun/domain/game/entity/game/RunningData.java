package com.ssafy.roCatRun.domain.game.entity.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunningData {
    private double distance = 0.0;
    private double currentSpeed = 0.0;
    private double pace = 0.0;
    private int calories = 0;
}