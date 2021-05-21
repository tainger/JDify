package io.terminus.dalaran.core.resource.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

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

    public Boolean setValueMinutes(String key, String value, long timeout) {
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

    public Boolean setValueSeconds(String key, String value, long timeout) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
    }

    public Boolean contains(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            return redisTemplate.hasKey(key);
        }
    }

    public Long incrKey(String key) {
        if(null == redisTemplate.opsForValue().get(key)) {
            persistKey(key, "0");
        }
        return redisTemplate.opsForValue().increment(key);
    }


    public Boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }


    public Long push(String queueName, String value) {
        return redisTemplate.opsForList().leftPush(queueName, value);
    }


    public String pop(String queueName) {
        return redisTemplate.opsForList().rightPop(queueName, 0, TimeUnit.MILLISECONDS);
    }

}
