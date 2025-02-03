package com.ssafy.roCatRun.domain.game.controller;

import com.ssafy.roCatRun.global.util.JwtTestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final JwtTestUtil jwtTestUtil;

    @GetMapping("/token/{userId}")
    public String generateTestToken(@PathVariable String userId) {
        return jwtTestUtil.generateToken(userId);
    }
}
