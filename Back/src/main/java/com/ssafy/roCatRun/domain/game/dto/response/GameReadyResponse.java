package com.ssafy.roCatRun.domain.game.dto.response;

import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameReadyResponse {
    String message;
    List<Player> players;
}
