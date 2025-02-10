package com.ssafy.roCatRun.global.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.ssafy.roCatRun.domain.game.service.manager.GameRoomManager;
import com.ssafy.roCatRun.domain.game.service.manager.GameTimerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {
    @Bean
    public GameTimerManager gameTimerManager(SocketIOServer server, GameRoomManager gameRoomManager) {
        return new GameTimerManager(server, gameRoomManager);
    }
}
