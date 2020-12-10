package io.terminus.dalaran.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CacheManager {

    private final Cache<String, String> cache = Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public boolean contains(String key) {
        return cache.getIfPresent(key) != null;
    }

    public boolean put(String key, String value) {
        if (contains(key)) {
            return false;
        } else {
            cache.put(key, value);
            return true;
        }
    }

    public String get(String key) {
        return cache.getIfPresent(key);
    }
}
