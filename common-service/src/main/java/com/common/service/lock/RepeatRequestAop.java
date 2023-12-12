package com.common.service.lock;

import com.common.service.handler.OperationException;
import com.google.common.base.Throwables;
import com.example.common.exception.MessageEnum;
import com.example.common.redis.RedisCache;
import com.common.service.dto.Response;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 重复请求AOP实现
 *
 * @author linjuliang
 */
@Slf4j
@Aspect
@Component
public class RepeatRequestAop {

    @Autowired
    private RedisCache redisCache;

    /**
     * spEL表达式解析器
     */
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardReflectionParameterNameDiscoverer discoverer = new StandardReflectionParameterNameDiscoverer();


    /**
     * 使用 Around 模式,在执行业务方法前加分布式锁(redis实现),执行完或者指定时长内进行锁释放
     *
     * @param point
     * @param repeatReq
     * @return
     */
    @Around("@annotation(repeatReq)")
    public Response<Void> doAround(ProceedingJoinPoint point, RepeatReq repeatReq) throws Throwable {
        String token = UUID.randomUUID().toString();
        // 解析spEL表达式
        String repeatKey = repeatReq.pre() + (StringUtils.isBlank(repeatReq.key()) ? "" : parseSpEl(getMethod(point), point.getArgs(), repeatReq.key()));
        try {
            boolean lock = redisCache.lock(repeatKey, token, repeatReq.timeOut());
            if (lock) {
                // 获取锁后执行业务方法
                return (Response) point.proceed();
            } else {
                // 返回请求重复提示
                return Response.failure(repeatReq.message());
            }
        } catch (RedisException e) {
            // 只捕获redis获取锁相关的异常,其他的异常向上抛出,在其他地方处理
            log.error(Throwables.getStackTraceAsString(e));
            return Response.failure(MessageEnum.REDIS_EXCEPTION);
        } finally {
            redisCache.unlock(repeatKey, token);
        }
    }


    /**
     * @param method
     * @param arguments
     * @param el
     * @return
     */
    private String parseSpEl(Method method, Object[] arguments, String el) {
        if (method == null) {
            return null;
        }
        String[] params = discoverer.getParameterNames(method);
        if (params == null) {
            throw new OperationException("参数列表不能为null");
        }
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < arguments.length; len++) {
            context.setVariable(params[len], arguments[len]);
        }
        try {
            Expression expression = parser.parseExpression(el);
            return expression.getValue(context, String.class);
        } catch (SpelParseException e) {
            return null;
        }
    }

    /**
     * 这里的定义决定了@RepeatReq目前只支持方法级
     *
     * @param point
     * @return
     */
    private Method getMethod(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Object target = point.getTarget();
        try {
            return target.getClass().getMethod(signature.getName(), signature.getParameterTypes());
        } catch (NoSuchMethodException e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
        return null;
    }


}
