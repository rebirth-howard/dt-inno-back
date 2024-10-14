package com.deutschmotors.moduleapi.common.redis;

import com.deutschmotors.modulecommon.jwt.TokenHelper;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@DependsOn("tokenHelper")
@Component
public class RedisCacheInitializer {

    private final TokenHelper tokenHelper;

    public RedisCacheInitializer(TokenHelper tokenHelper) {
        this.tokenHelper = tokenHelper;
        initializeKeys();
    }

    private void initializeKeys() {
        for (RedisCache apiRedisKey : RedisCache.values()) {
            tokenHelper.addRedisKey(apiRedisKey.toRedisKey());
        }
    }
}
