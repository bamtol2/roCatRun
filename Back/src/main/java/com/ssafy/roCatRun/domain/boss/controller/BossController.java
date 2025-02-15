package com.ssafy.roCatRun.domain.boss.controller;

import com.ssafy.roCatRun.domain.boss.dto.response.BossResponse;
import com.ssafy.roCatRun.domain.boss.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boss")
@RequiredArgsConstructor
public class BossController {
    private final BossService bossService;

    @GetMapping
    public ResponseEntity<BossResponse> getBossInfo(){
        return ResponseEntity.ok(bossService.getBossInfo());
    }
}
