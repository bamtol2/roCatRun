package com.ssafy.raidtest.raid.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DistanceUpdateRequest {
    private double distance; // 러닝 거리
    private double latitude; // 위도
    private double longitude; // 경도
    private double speed;
    private LocalDateTime timestamp;
}
