package com.huagui.service.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: 定时任务锁注解
 * @Description:
 * @Author: chenx
 * @Date: 2019/5/19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LockAspect {

    String value() default "";

    String lockKey() default "";

}
