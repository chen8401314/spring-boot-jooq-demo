package com.huagui.gateway;

import com.huagui.service.base.ServiceName;
import com.huagui.service.base.context.CommonException;
import com.huagui.service.base.context.ServiceContext;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static com.huagui.gateway.FilterCommon.extractPath;
import static com.huagui.gateway.FilterCommon.skipRequest;

@Slf4j
public class ServiceAuthorizationFilter implements GlobalFilter, Ordered, CommandLineRunner {

    private WebClient client;

    @Autowired
    CircuitBreakerConfig circuitBreakerConfig;

    private CircuitBreakerOperator<Boolean> circuitBreaker;

    private static final String authService = "lb://" + ServiceName.USER_SERVICE;
    private static final String authUrl = "/user/v1/access/hasPermission";

    @Autowired
    WebClient.Builder adaptedWebClient;

    @Override
    public void run(String... args) throws Exception {
        this.client = adaptedWebClient
                .baseUrl(authService)
                .build();

        circuitBreaker = CircuitBreakerOperator.of(CircuitBreaker.of("auth-has-permission", circuitBreakerConfig));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (skipRequest(request)) {
            return chain.filter(exchange);
        }

        HttpHeaders headers = request.getHeaders();
        String path = extractPath(request);

        return client.get()
                .uri(builder -> builder.path(authUrl).queryParam("path", path).build())
                .header(ServiceContext.AUTHENTICATED_HEADER, headers.getFirst(ServiceContext.AUTHENTICATED_HEADER))
                .retrieve().bodyToMono(Boolean.class).timeout(Duration.ofSeconds(2))
                .transformDeferred(circuitBreaker)
                .flatMap(r -> {
                    log.debug("****** Got:{} for path:{} ****** ", r, path);
                    if (Boolean.TRUE.equals(r)) {
                        return chain.filter(exchange);
                    }
                    return Mono.error(new CommonException(403, "You don't have access", 403));
                })
                .onErrorMap(ex -> {
                    if (ex instanceof CommonException) {
                        return ex;
                    }
                    return new CommonException(500, "Service Unavailable", ex, 500);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500 * 4;
    }
}
