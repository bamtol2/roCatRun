package com.ssafy.roCatRun.domain.game.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticateRequest {
    private String token;
}