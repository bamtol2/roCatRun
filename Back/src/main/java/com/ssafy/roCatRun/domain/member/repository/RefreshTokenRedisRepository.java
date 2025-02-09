package com.ssafy.roCatRun.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis에 RefreshToken을 저장하고 관리하는 Repository
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis에 저장되는 RefreshToken의 key prefix
     * RT:{key} 형태로 저장됨
     */
    private final static String KEY_PREFIX = "RT:";

    /**
     * RefreshToken을 Redis에 저장
     * @param key 사용자 식별자 (memberId)
     * @param refreshToken JWT RefreshToken
     * @param expiration 만료 시간 (milliseconds)
     */
    public void save(String key, String refreshToken, long expiration) {
        String redisKey = KEY_PREFIX + key;
        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 주어진 key에 해당하는 RefreshToken을 조회
     * @param key 사용자 식별자 (memberId)
     * @return RefreshToken (Optional)
     */
    public Optional<String> findByKey(String key) {
        String redisKey = KEY_PREFIX + key;
        String refreshToken = redisTemplate.opsForValue().get(redisKey);
        return Optional.ofNullable(refreshToken);
    }

    /**
     * 주어진 key에 해당하는 RefreshToken을 삭제
     * 로그아웃 또는 회원 탈퇴 시 호출됨
     * @param key 사용자 식별자 (memberId)
     */
    public void deleteByKey(String key) {
        String redisKey = KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}