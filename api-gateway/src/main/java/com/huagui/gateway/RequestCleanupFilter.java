package com.huagui.gateway;

import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.huagui.gateway.FilterCommon.ANONYMOUS;

@Slf4j
public class RequestCleanupFilter implements GlobalFilter, Ordered {

    private List<String> blackUrl = List.of("GET:/user/user/refreshToken");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = FilterCommon.extractPath(request);
        if (blackUrl.contains(path)) {
            log.error("Accessing private uri {} from {}", path, Optional.ofNullable(request).map(ServerHttpRequest::getRemoteAddress).map(InetSocketAddress::getAddress).map(InetAddress::getHostAddress).orElse(""));
            return Mono.error(new CommonException(404, "Page doesn't exist"));
        }

        ServerHttpRequest newRequest = request.mutate()
                .headers(httpHeaders -> httpHeaders.remove(ANONYMOUS))
                .header(ServiceContext.TRACE_ID_HEADER, UUID.randomUUID().toString())
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
