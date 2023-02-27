package com.huagui.gateway;

import com.google.common.base.Throwables;
import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.ServiceContext;
import com.huagui.common.base.context.UserToken;
import com.huagui.common.base.util.JWTUtils;
import com.huagui.common.config.LocalRedisCache;
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
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.huagui.gateway.FilterCommon.skipRequest;

@Slf4j
public class TokenAuthenticationFilter implements GlobalFilter, Ordered, CommandLineRunner {

    @Autowired
    @Qualifier("localAuthVersion")
    LocalRedisCache<String, Integer, String> localAuthVersion;

    @Autowired
    WebClient.Builder adaptedWebClient;

    @Autowired
    CircuitBreakerConfig circuitBreakerConfig;

    private CircuitBreakerOperator<String> authCircuitBreaker;

    private WebClient client;
    static List<String> domains;
    static String authService = "lb://user-service:";
    private static final String REFRESH_TOKEN_URL = "/user/user/refreshToken";

    static {
        String userServicePort = System.getenv("USER_SERVICE_PORT");
        String authCookieDomain = System.getenv("AUTH_COOKIE_DOMAIN");
        if (StringUtils.isEmpty(authCookieDomain)) {
            authCookieDomain = "localhost";
        }
        if (StringUtils.isEmpty(userServicePort)) {
            userServicePort = "8080";
        }
        domains = Arrays.asList(authCookieDomain.split(","));
        authService = String.format("%s%s", authService, userServicePort);
    }

    @Override
    public void run(String... args) throws Exception {
        this.client = adaptedWebClient
                .baseUrl(authService)
                .build();

        authCircuitBreaker = CircuitBreakerOperator.of(CircuitBreaker.of("auth-refresh-token", circuitBreakerConfig));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        if (skipRequest(request)) {
            return chain.filter(exchange);
        }

        //all other cases are accessing protected resources
        HttpCookie authSessionCookie = request.getCookies().getFirst(ServiceContext.TOKEN_HEADER);
        String jwtStr = null;
        if (authSessionCookie != null) {
            jwtStr = authSessionCookie.getValue();
        }

        if (StringUtils.isEmpty(jwtStr)) {
            HttpHeaders headers = request.getHeaders();
            jwtStr = headers.getFirst(ServiceContext.TOKEN_HEADER);

            if (StringUtils.isEmpty(jwtStr)) {
                return Mono.error(new CommonException(401, "Please login", 401));
            }
        }

        //validate the token
        UserToken user = JWTUtils.extractToken(jwtStr);

        // the Token is gonna expire in 4hours, help user to refresh token
        if (LocalDateTime.now().plusHours(4).isAfter(user.getExpireAt())) {
            log.info("refreshUserToken +++++++++++++++++++++++++ refreshUserToken");
            return refreshUserToken(user, exchange, chain);
        }

//        Integer userAuthVersion = localAuthVersion.getValue(user.getId());
//        if (userAuthVersion == null) {
//            // extract from redis
//            return redisTemplate.opsForValue().get(user.getId())
//                    .timeout(Duration.ofSeconds(1))
//                    .transformDeferred(redisCircuitBreaker)
//                    .onErrorResume(ex -> {
//                        log.error("Can't access redis: {}", Throwables.getStackTraceAsString(ex));
//                        return Mono.empty();
//                    })
//                    .defaultIfEmpty(user.getVersion().toString())
//                    .map(Integer::parseInt)
//                    .flatMap(version -> {
//                        localAuthVersion.put(user.getId(), version);
//                        return refreshTokenOrContinue(version, user, exchange, chain);
//                    });
//        }
//        return refreshTokenOrContinue(userAuthVersion, user, exchange, chain);
        return continueFilter(user, exchange, chain);
    }

    Mono<Void> refreshTokenOrContinue(Integer version, UserToken user, ServerWebExchange exchange, GatewayFilterChain chain) {
        if (version > user.getVersion()) {
            return refreshUserToken(user, exchange, chain);
        }
        return continueFilter(user, exchange, chain);
    }

    Mono<Void> continueFilter(UserToken user, ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .header(ServiceContext.AUTHENTICATED_HEADER, user.toJson())
                .build();

        ServerWebExchange newExchange = exchange.mutate()
                .request(newRequest).principal(Mono.just(user)).build();

        return chain.filter(newExchange);
    }

    Mono<Void> refreshUserToken(UserToken oldToken, ServerWebExchange exchange, GatewayFilterChain chain) {
        return client.get()
                .uri(REFRESH_TOKEN_URL)
                .header(ServiceContext.AUTHENTICATED_HEADER, oldToken.toJson())
                .retrieve().bodyToMono(String.class).timeout(Duration.ofSeconds(2))
                .transformDeferred(authCircuitBreaker)
                .onErrorResume(ex -> {
                    log.error("Error in refresh user Token: {}", Throwables.getStackTraceAsString(ex));
                    return Mono.empty();
                })
                .defaultIfEmpty("error")
                .flatMap(newToken -> {
                    if ("error".equals(newToken)) {
                        return continueFilter(oldToken, exchange, chain);
                    }
                    domains.forEach(domain ->
                                    exchange.getResponse()
                                            .addCookie(ResponseCookie.from(ServiceContext.TOKEN_HEADER, newToken)
                                                    .domain(domain)
                                                    .path("/")
                                                    .httpOnly(true)
//                                    .secure(true)
                                                    .build())
                    );

                    UserToken newUser = JWTUtils.extractToken(newToken);
//                    localAuthVersion.put(newUser.getId(), newUser.getVersion());
                    return continueFilter(newUser, exchange, chain);
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500;
    }
}
