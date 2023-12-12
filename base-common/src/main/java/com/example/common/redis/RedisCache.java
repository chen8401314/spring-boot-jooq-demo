package com.example.common.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LOCK_PREFIX = "REDIS_LOCK:";

    public Boolean del(String key) {
        return redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, Integer ttl) {
        set(key, value);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Long batchDel(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void setObj(String key, Object value) {
        redisTemplate.opsForValue().set(key, Objects.requireNonNull(SerializationUtils.serialize(value)));
    }

    public void setObj(String key, Object value, Integer ttl) {
        setObj(key, value);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public Object getObj(String key) {
        return SerializationUtils.deserialize((byte[]) redisTemplate.opsForValue().get(key));
    }

    /**
     * 释放锁
     *
     * @param key
     * @param token
     * @return
     */
    public Boolean unlock(String key, String token) {
        String currentValue = get(LOCK_PREFIX + key);
        if (token.equals(currentValue)) {
            return redisTemplate.delete(LOCK_PREFIX + key);
        } else {
            return false;
        }
    }


    /**
     * 加锁（用于定时任务）
     *
     * @param key     锁的key
     * @param timeout 锁超时时间
     * @return
     */
    public boolean lock(String key, String token, int timeout) {
        Boolean b = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, token, timeout, TimeUnit.MILLISECONDS);
        if (b != null) {
            return b;
        }
        return false;
    }


}


