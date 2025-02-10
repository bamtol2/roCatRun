package com.ssafy.roCatRun.domain.game.entity.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {
    private String id;
    private String nickname;
    private String email;
    private UserStatus status = UserStatus.OFFLINE;
    private LocalDateTime lastLoginAt;
}
