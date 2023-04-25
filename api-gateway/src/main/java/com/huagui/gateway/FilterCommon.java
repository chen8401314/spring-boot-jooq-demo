package com.huagui.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface FilterCommon {
    String ANONYMOUS = "anonymous";
    String AUTHORIZED = "authorized";

    static String extractPath(ServerHttpRequest request) {
        String url = request.getURI().getPath();
        HttpMethod method = request.getMethod();
        return method + ":" + url;
    }

    static boolean skipRequest(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        if (Boolean.TRUE.equals(Boolean.valueOf(headers.getFirst(ANONYMOUS)))) {
            return true;
        }

        return Boolean.TRUE.equals(Boolean.valueOf(headers.getFirst(AUTHORIZED)));
    }


}
