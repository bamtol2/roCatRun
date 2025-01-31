package com.ssafy.raidtest.raid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Reward {
    private RewardType type;
    private int amount;
}
