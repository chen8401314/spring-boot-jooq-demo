package com.common.service.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 只能用于方法
 *
 * @author linjuliang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RepeatReq {

    /**
     * @return 前缀
     */
    String pre() default "";

    /**
     * @return 支持spEL表达式的解析
     */
    String key() default "";

    /**
     * 失效时长的默认值主要考虑方法耗时过长或其他原因导致锁一直不释放,其他请求一直无法执行的情况
     *
     * @return 重复的过期时长, 默认10秒;如果执行方法超过默认时长,需要自定义,否则10秒后重复请求不拦截
     */
    int timeOut() default 10000;

    /**
     * @return 重复的请求的提示信息, 业务决定
     */
    String message() default "请求重复";


}
