package com.ssafy.roCatRun.domain.game.entity.raid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RunningData implements Serializable {
    private double distance = 0.0;
    private double currentSpeed = 0.0;
}