package com.ssafy.raidtest.raid.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeverMessage {
    private String gameId;
    private double requiredDistance;
    private int duration;
}

