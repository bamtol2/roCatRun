package com.ssafy.roCatRun.domain.game.dto.response;

import com.ssafy.roCatRun.domain.game.entity.raid.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class GameStartResponse {
    String roomId;
    String message;
    int bossHp;
    int timeLimit;
    List<SimplePlayer> players;

    @Getter
    @AllArgsConstructor
    static class SimplePlayer{
        private String id;
        private String nickname;
    }

    public static GameStartResponse of(String roomId, String message, int bossHp, int timeLimit, List<Player> players) {
        List<SimplePlayer> simplePlayers = players.stream()
                .map(player -> new SimplePlayer(player.getId(), player.getNickname()))
                .collect(Collectors.toList());

        return new GameStartResponse(roomId, message, bossHp, timeLimit, simplePlayers);
    }
}
