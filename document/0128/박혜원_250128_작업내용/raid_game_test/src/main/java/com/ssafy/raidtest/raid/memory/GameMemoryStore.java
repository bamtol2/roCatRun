package com.ssafy.raidtest.raid.memory;

import com.ssafy.raidtest.raid.domain.game.RaidGame;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// 인메모리에 유저들의 실시간 정보 저장 - 싱글턴
@Component
public class GameMemoryStore {
    private final Map<String, RaidGame> gameMap = new ConcurrentHashMap<>();

    // 게임 저장
    public void saveGame(String gameId, RaidGame game){
        gameMap.put(gameId, game);
    }

    // 아이디를 통한 게임 정보 조회
    public Optional<RaidGame> getGame(String gameId){
        return Optional.ofNullable(gameMap.get(gameId));
    }

    // 게임 종료 시 데이터 삭제
    public void removeGame(String gameId){
        gameMap.remove(gameId);
    }
}
