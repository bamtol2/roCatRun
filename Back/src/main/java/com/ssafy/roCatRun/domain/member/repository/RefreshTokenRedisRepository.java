package com.ssafy.roCatRun.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String KEY_PREFIX = "RT:";

    public void save(String key, String refreshToken, long expiration) {
        String redisKey = KEY_PREFIX + key;
        redisTemplate.opsForValue().set(
                redisKey,
                refreshToken,
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    public Optional<String> findByKey(String key) {
        String redisKey = KEY_PREFIX + key;
        String refreshToken = redisTemplate.opsForValue().get(redisKey);
        return Optional.ofNullable(refreshToken);
    }

    public void deleteByKey(String key) {
        String redisKey = KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}