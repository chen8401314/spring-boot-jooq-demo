package com.huagui.common.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.*;
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
        Boolean b = redisTemplate.hasKey(key);
        if (b == null) {
            return false;
        }
        return b;
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


    /**
     * 批量查询
     *
     * @param keys
     * @param useParallel 是否使用parallel 在没有顺序要求的时候,提高效率,true为表示使用,false 表示不用,默认为true
     * @return
     */
    public Map<String, Object> batchQueryByKeys(List<String> keys, Boolean useParallel) {
        Map<String, Object> resultMap = Maps.newHashMap();
        if (null == keys || keys.isEmpty()) {
            return resultMap;
        }
        if (null == useParallel) {
            useParallel = true;
        }
        List<Object> results = redisTemplate.executePipelined(
                (RedisCallback<Object>) connection -> {
                    for (String key : keys) {
                        connection.get(key.getBytes());
                    }
                    return null;
                });
        if (results.isEmpty()) {
            return resultMap;
        }
        if (useParallel) {
            Map<String, Object> resultMapOne = Collections.synchronizedMap(new HashMap<>());
            keys.parallelStream().forEach(t -> {
                Object value = results.get(keys.indexOf(t));
                if (value != null) {
                    resultMapOne.put(t, SerializationUtils.deserialize((byte[]) value));
                }
            });
            resultMap = resultMapOne;
        } else {
            Map<String, Object> resultMapTwo = new HashMap<>();
            for (String t : keys) {
                Object value = results.get(keys.indexOf(t));
                if (value == null) {
                    continue;
                }
                resultMapTwo.put(t, SerializationUtils.deserialize((byte[]) value));
            }
            resultMap = resultMapTwo;
        }
        return resultMap;
    }

    /**
     * 批量查询
     *
     * @param keys
     * @return
     */
    public List<Object> multiGet(List<String> keys) {
        return this.redisTemplate.opsForValue().multiGet(keys);
    }


}


