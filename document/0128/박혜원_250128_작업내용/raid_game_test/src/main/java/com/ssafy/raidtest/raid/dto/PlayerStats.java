package com.ssafy.raidtest.raid.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlayerStats {
    private double distance;
    private int itemsUsed;
    private double damageDealt;
}
