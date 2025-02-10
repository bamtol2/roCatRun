package com.ssafy.roCatRun.domain.game.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerDisconnectedResponse {
    private String userId;
    private long reconnectionTimeout; // 초단위
}
