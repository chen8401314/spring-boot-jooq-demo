package com.huagui.gateway;


import com.huagui.service.base.context.CommonException;
import com.huagui.service.base.context.UserToken;
import com.huagui.service.config.LocalRedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.huagui.gateway.FilterCommon.*;


@Slf4j
public class LocalCacheAuthorizationFilter implements GlobalFilter, Ordered {

    @Autowired
    @Qualifier("localAuthCache")
    private LocalRedisCache<String, Long, String> localAuthCache;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (skipRequest(request)) {
            return chain.filter(exchange);
        }

        String path = extractPath(request);

        return exchange.getPrincipal()
                .cast(UserToken.class)
                .flatMap(u -> {
                    Long uriCode = localAuthCache.getValue(path);
                    if (uriCode != null && u.getCode() != null) {
                        log.debug("***Local*** name:{}, uriCode:{}, tokenUriCode:{}, path:{} ****** ", u.getName(), uriCode, u.getCode(), path);
                        if ((u.getCode() & uriCode) != 0) {
                            return FilterCommon.checkAuthorizationStatus(Boolean.TRUE, exchange, chain);
                        }
                        return Mono.error(new CommonException(403, "You don't have access", 403));
                    }
                    return FilterCommon.checkAuthorizationStatus(Boolean.FALSE, exchange, chain);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500 * 2;
    }
}
