package com.ssafy.roCatRun.global.socket;

import com.ssafy.roCatRun.domain.game.entity.user.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {
    // 세션ID를 키로 하는 유저 세션 맵
    private final ConcurrentHashMap<String, UserSession> sessions = new ConcurrentHashMap<>();
    // 유저ID를 키로 하는 세션ID 맵 (역방향 조회용)
    private final ConcurrentHashMap<String, String> userToSessionMap = new ConcurrentHashMap<>();

    /**
     * 새로운 유저 세션을 생성
     * @param userId 유저 식별자
     * @param socketId 웹소켓 연결 식별자
     */
    public void createSession(String userId, String socketId) {
        UserSession session = new UserSession(userId, socketId, System.currentTimeMillis());
        sessions.put(socketId, session);
        userToSessionMap.put(userId, socketId);
        log.info("Session created for user: {}", userId);
    }

    /**
     * 세션ID로 세션 조회
     */
    public Optional<UserSession> getSession(String socketId) {
        return Optional.ofNullable(sessions.get(socketId));
    }

    /**
     * 유저ID로 세션 조회
     */
    public Optional<UserSession> getSessionByUserId(String userId) {
        String socketId = userToSessionMap.get(userId);
        return socketId != null ? Optional.ofNullable(sessions.get(socketId)) : Optional.empty();
    }

    /**
     * 세션 제거 (연결 종료 시)
     */
    public void removeSession(String socketId) {
        UserSession session = sessions.remove(socketId);
        if (session != null) {
            userToSessionMap.remove(session.getUserId());
            log.info("Session removed for user: {}", session.getUserId());
        }
    }

    /**
     * 특정 유저의 연결 상태 확인
     */
    public boolean isConnected(String userId) {
        return userToSessionMap.containsKey(userId);
    }
}
