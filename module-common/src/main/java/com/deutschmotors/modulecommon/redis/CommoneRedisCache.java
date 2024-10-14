package com.deutschmotors.modulecommon.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
public class CommoneRedisCache {
    public static final CommoneRedisCache DEFAULT = new CommoneRedisCache("default", 30, TimeUnit.MINUTES); // 기본 TTL 설정
    public static final CommoneRedisCache JWT_ACCESS_TOKEN = new CommoneRedisCache("jwtAccessToken", 5, TimeUnit.MINUTES); // 기본 TTL 설정
    public static final CommoneRedisCache JWT_REFRESH_TOKEN = new CommoneRedisCache("jwtRefreshToken", 1, TimeUnit.HOURS); // 기본 TTL 설정

    private final String key;
    private final long duration;
    private final TimeUnit unit;
}
