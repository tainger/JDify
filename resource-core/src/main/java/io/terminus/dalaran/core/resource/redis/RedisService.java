package io.terminus.dalaran.core.resource.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    private final String split = ":";

    private final String ALARM_ID = "alarm_id";


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public Boolean persistKey(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        return redisTemplate.persist(key);
    }


    public Boolean setValue(String key, String value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            return redisTemplate.opsForValue().setIfAbsent(key, value, 30, TimeUnit.MINUTES);
        }
    }

    public Boolean setValue(String key, String value, long timeout) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MINUTES);
        } catch (Exception e) {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.MINUTES);
        }
    }

    public String getValue(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return redisTemplate.opsForValue().get(key);
        }
    }

    public Boolean setValue(String key, String value, Long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    public Boolean contains(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            return redisTemplate.hasKey(key);
        }
    }

    public Boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }
}
