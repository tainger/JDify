package io.terminus.dalaran.core.resource.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Boolean setValue(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, 30, TimeUnit.MINUTES);
    }

    public Boolean contains(String key) {
        return redisTemplate.hasKey(key);
    }
}
