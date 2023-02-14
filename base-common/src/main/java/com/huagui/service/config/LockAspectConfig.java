package com.huagui.service.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Title:定时任务锁aop
 * @Description:
 * @Author: chenx
 * @Date: 2019/5/19
 */
@Slf4j
@Aspect
@Component
public class LockAspectConfig {

    @Autowired
    private RedisCache redisCache;

    /**
     * @param proceedingJoinPoint
     * @return
     */
    @Around("@annotation(com.huagui.service.config.LockAspect)")
    public Object doAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Object obj = null;
        try {
            Signature signature = proceedingJoinPoint.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            if (method != null) {
                LockAspect lockAspect = method.getAnnotation(LockAspect.class);
                boolean lock = false;
                String lockKey = lockAspect.lockKey();
                String token = UUID.randomUUID().toString();
                try {
                    // 定时任务中最慢的可能需要两个小时
                    lock = redisCache.lock(lockKey, token, 2 * 60 * 60 * 1000);
                    if (lock) {
                        log.info("获得锁:{}", lockKey);
                        obj = proceedingJoinPoint.proceed();
                    } else {
                        log.info("没有获取到锁:{}", lockKey);
                    }
                } finally {
                    if (lock) {
                        redisCache.unlock(lockKey, token);
                        log.info("任务结束，释放锁{}!", lockKey);
                    }
                }
            }
        } catch (Throwable throwable) {
            log.error("获取锁异常：{}", throwable.getMessage());
        }
        return obj;
    }

}
