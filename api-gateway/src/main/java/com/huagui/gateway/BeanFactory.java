package com.huagui.gateway;


import com.huagui.common.base.util.JsonObjectConverter;
import com.huagui.common.config.LocalRedisCache;
import com.huagui.common.config.RedisConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Principal;
import java.time.Duration;


@Configuration(proxyBeanMethods = false)
@Slf4j
@Import({RedisConfig.class})
@EnableWebFlux
public class BeanFactory implements WebFluxConfigurer {

    /**
     * 超时时间
     */
    @Value("${timeoutSecond}")
    private String timeoutSecond;


    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs()
                .jackson2JsonDecoder(new Jackson2JsonDecoder(JsonObjectConverter.getObjectMapper()));
        configurer.defaultCodecs()
                .jackson2JsonEncoder(new Jackson2JsonEncoder(JsonObjectConverter.getObjectMapper()));
    }

//    @Bean
//    public WebFilter contextPathWebFilter() {
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//            if (request.getURI().getPath().startsWith(contextPath)) {
//                return chain.filter(
//                        exchange.mutate()
//                                .request(request.mutate().contextPath(contextPath).build())
//                                .build());
//            }
//            return chain.filter(exchange);
//        };
//    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public AccessLogFilter accessLogFilter() {
        return new AccessLogFilter();
    }

    @Bean
    RequestCleanupFilter requestHeaderCleanupFilter() {
        return new RequestCleanupFilter();
    }

    @Bean
    OrderedAddRequestHeaderGatewayFilterFactory orderedAddRequestHeaderFactory() {
        return new OrderedAddRequestHeaderGatewayFilterFactory();
    }

//    @Bean
//    LocalCacheAuthorizationFilter localCacheAuthorizationFilter() {
//        return new LocalCacheAuthorizationFilter();
//    }
//
//    @Bean
//    RedisCacheAuthorizationFilter redisCacheAuthorizationFilter() {
//        return new RedisCacheAuthorizationFilter();
//    }
//
//    @Bean
//    ServiceAuthorizationFilter serviceAuthorizationFilter() {
//        return new ServiceAuthorizationFilter();
//    }

    @Bean("localAuthCache")
    public LocalRedisCache<String, Long, String> localAuthCache() {
        return new LocalRedisCache<>() {

            @Override
            protected String buildKey(String message) {
                return message.split("@")[1];
            }

            @Override
            protected Long buildValue(String message) {
                return Long.parseLong(message.split("@")[0]);
            }

            @Override
            public String[] getChannels() {
                return new String[]{"USER_AUTHORIZATION"};
            }

        };
    }

    @Bean("localAuthVersion")
    public LocalRedisCache<String, Integer, String> localAuthVersion() {
        return new LocalRedisCache<>() {

            @Override
            protected String buildKey(String message) {
                return message.split(":")[0];
            }

            @Override
            protected Integer buildValue(String message) {
                return Integer.parseInt(message.split(":")[1]);
            }

            @Override
            public String[] getChannels() {
                return new String[]{"USER_TOKEN_VERSION"};
            }

            @Override
            protected int maxSize() {
                return 1000000;
            }

        };
    }

    @Bean
    @Primary
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName);
    }

    @Bean
    public CircuitBreakerConfig circuitBreakerConfig() {
        return CircuitBreakerConfig.custom()
                .failureRateThreshold(65)
                .slowCallRateThreshold(65)
                .waitDurationInOpenState(Duration.ofSeconds(8))
                .slowCallDurationThreshold(Duration.ofSeconds(6))
                .permittedNumberOfCallsInHalfOpenState(3)
                .minimumNumberOfCalls(5)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .slidingWindowSize(10)
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(CircuitBreakerConfig circuitBreakerConfig) {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(Long.parseLong(timeoutSecond))).build())
                .circuitBreakerConfig(circuitBreakerConfig)
                .build());
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder adaptedWebClient() {
        return WebClient.builder();
    }
}
