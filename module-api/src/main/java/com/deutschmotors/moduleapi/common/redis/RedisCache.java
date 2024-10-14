package com.deutschmotors.moduleapi.common.redis;

import com.deutschmotors.modulecommon.redis.CommoneRedisCache;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public enum RedisCache {
    JWT_ACCESS_TOKEN("jwtAccessToken", 5, TimeUnit.MINUTES),
    JWT_REFRESH_TOKEN("jwtRefreshToken", 1, TimeUnit.HOURS);

    private final String key;
    private final long duration;
    private final TimeUnit unit;

    public CommoneRedisCache toRedisKey() {
        return new CommoneRedisCache(key, duration, unit);
    }

}
