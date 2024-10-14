package com.deutschmotors.modulecommon.redis;

import java.util.concurrent.TimeUnit;

public interface RedisCacheIfs {
    long duration();
    TimeUnit unit();

    static <T extends Enum<T> & RedisCacheIfs> T getByName(Class<T> enumType, String name) {
        for (T cache : enumType.getEnumConstants()) {
            if (cache.name().equalsIgnoreCase(name)) {
                return cache;
            }
        }
        throw new IllegalArgumentException("No enum constant " + name);
    }

}
