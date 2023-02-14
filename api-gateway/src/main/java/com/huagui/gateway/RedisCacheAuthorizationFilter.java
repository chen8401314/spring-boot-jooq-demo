package com.huagui.gateway;

import com.google.common.base.Throwables;
import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.UserToken;
import com.huagui.service.config.LocalRedisCache;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.huagui.gateway.FilterCommon.*;

@Slf4j
public class RedisCacheAuthorizationFilter implements GlobalFilter, Ordered, CommandLineRunner {

    @Autowired
    ReactiveStringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("localAuthCache")
    private LocalRedisCache<String, Long, String> localAuthCache;


    @Autowired
    CircuitBreakerConfig circuitBreakerConfig;

    private CircuitBreakerOperator<String> circuitBreaker;

    @Override
    public void run(String... args) throws Exception {
        circuitBreaker  = CircuitBreakerOperator.of(CircuitBreaker.of("redis-fetch-uriCode", circuitBreakerConfig));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (skipRequest(request)) {
            return chain.filter(exchange);
        }
        String path = extractPath(request);

        return Mono.zip(exchange.getPrincipal().cast(UserToken.class),
                redisTemplate.opsForValue().get(path)
                        .timeout(Duration.ofSeconds(1))
                        .transformDeferred(circuitBreaker)
                        .onErrorResume(ex -> {
                            log.error("Error fetching uriCode: {}", Throwables.getStackTraceAsString(ex));
                            return Mono.empty();
                        })
                        .defaultIfEmpty("FALSE"))
                .flatMap(t -> {
                    if ("FALSE".equals(t.getT2())) {
                        return FilterCommon.checkAuthorizationStatus(Boolean.FALSE, exchange, chain);
                    }

                    long uriCode = Long.parseLong(t.getT2());
                    if (t.getT1().getCode() != null) {
                        log.debug("****** name:{}, uriCode:{}, tokenUriCode:{}, path:{} ****** ", t.getT1().getName(), uriCode, t.getT1().getCode(), path);
                        if ((t.getT1().getCode() & uriCode) != 0) {
                            localAuthCache.put(path, uriCode);
                            return FilterCommon.checkAuthorizationStatus(Boolean.TRUE, exchange, chain);
                        }
                        return Mono.error(new CommonException(403, "You don't have access", 403));
                    }
                    return FilterCommon.checkAuthorizationStatus(Boolean.FALSE, exchange, chain);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500 * 3;
    }
}
