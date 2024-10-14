package com.deutschmotors.modulecommon.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisCacheHelper {

    private final RedisTemplate<String, Object> redisTemplate;

    // Redis에서 문자열 값을 가져오는 메서드
    public String getStringValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    // Redis에 문자열 값을 저장하는 메서드 (TTL을 설정)
    public void setStringValue(String key, String value, long duration, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, duration, unit);
    }

    // Redis에서 특정 키 삭제
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    // Redis에서 Set 자료형 값을 추가하는 메서드
    public void addToSet(String key, String value, long duration, TimeUnit unit) {
        redisTemplate.opsForSet().add(key, value);
        redisTemplate.expire(key, duration, unit);
    }

    // Redis에서 Set 자료형 값을 가져오는 메서드
    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // Redis에 Hash 자료형 값을 저장하는 메서드
    public void putToHash(String key, String hashKey, Object value, long duration, TimeUnit unit) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        redisTemplate.expire(key, duration, unit);
    }

    // Redis에서 Hash 자료형 값을 가져오는 메서드
    public Object getFromHash(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    // Redis에서 모든 Hash 값을 가져오는 메서드
    public Map<Object, Object> getAllFromHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
}
