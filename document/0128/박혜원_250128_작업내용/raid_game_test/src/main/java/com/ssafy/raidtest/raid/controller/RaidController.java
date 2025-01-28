package com.ssafy.raidtest.raid.controller;

import com.ssafy.raidtest.raid.domain.room.RaidRoom;
import com.ssafy.raidtest.raid.domain.statistics.StatisticsPeriod;
import com.ssafy.raidtest.raid.dto.GameStatistics;
import com.ssafy.raidtest.raid.dto.request.CreateRoomRequest;
import com.ssafy.raidtest.raid.dto.request.JoinRoomRequest;
import com.ssafy.raidtest.raid.dto.request.MatchRequest;
import com.ssafy.raidtest.raid.service.GameService;
import com.ssafy.raidtest.raid.service.MatchingService;
import com.ssafy.raidtest.raid.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raid")
@RequiredArgsConstructor
public class RaidController {
    private final MatchingService matchingService;
    private final StatisticsService statisticsService;

    @PostMapping("/rooms")
    public ResponseEntity<RaidRoom> createRoom(@RequestBody CreateRoomRequest request) {
        RaidRoom room = matchingService.createRoom(
                request.getUserId(),
                request.getBoss(),
                request.getMaxPlayers()
        );
        return ResponseEntity.ok(room);
    }

    @PostMapping("/rooms/{inviteCode}/join")
    public ResponseEntity<RaidRoom> joinByInviteCode(
            @PathVariable String inviteCode,
            @RequestBody JoinRoomRequest request) {
        RaidRoom room = matchingService.joinByInviteCode(request.getUserId(), inviteCode);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/match")
    public ResponseEntity<RaidRoom> matchGame(@RequestBody MatchRequest request) {
        RaidRoom room = matchingService.matchPlayer(
                request.getUserId(),
                request.getBoss(),
                request.getMaxPlayers()
        );
        return ResponseEntity.ok(room);
    }

    @GetMapping("/statistics/{userId}")
    public ResponseEntity<GameStatistics> getStatistics(
            @PathVariable String userId,
            @RequestParam StatisticsPeriod period) {
        GameStatistics statistics = statisticsService.getStatistics(userId, period);
        return ResponseEntity.ok(statistics);
    }
}

