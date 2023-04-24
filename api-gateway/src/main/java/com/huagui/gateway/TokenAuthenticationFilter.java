package com.huagui.gateway;

import com.google.common.base.Throwables;
import com.huagui.common.base.context.CommonException;
import com.huagui.common.base.context.ServiceContext;
import com.huagui.common.base.context.UserToken;
import com.huagui.common.base.util.JWTUtils;
import com.huagui.common.base.util.SerializeUtil;
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
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    @Autowired
    @Qualifier("localAuthVersion")
    LocalRedisCache<String, Integer, String> localAuthVersion;

    @Autowired
    WebClient.Builder adaptedWebClient;

    @Autowired
    CircuitBreakerConfig circuitBreakerConfig;

    static List<String> domains;

    static {
        String authCookieDomain = System.getenv("AUTH_COOKIE_DOMAIN");
        if (StringUtils.isEmpty(authCookieDomain)) {
            authCookieDomain = "localhost";
        }
        domains = Arrays.asList(authCookieDomain.split(","));
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

            String newToken = JWTUtils.createToken(user.getId(), user.getName());
            domains.forEach(domain ->
                    exchange.getResponse()
                            .addCookie(ResponseCookie.from(ServiceContext.TOKEN_HEADER, newToken)
                                    .domain(domain)
                                    .path("/")
                                    .httpOnly(true)
                                    .build())
            );
            UserToken newUser = JWTUtils.extractToken(newToken);
            return continueFilter(newUser, exchange, chain);
        }
        return continueFilter(user, exchange, chain);
    }


    Mono<Void> continueFilter(UserToken user, ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .header(ServiceContext.AUTHENTICATED_HEADER, SerializeUtil.serialize(user))
                .build();

        ServerWebExchange newExchange = exchange.mutate()
                .request(newRequest).principal(Mono.just(user)).build();

        return chain.filter(newExchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 500;
    }
}
