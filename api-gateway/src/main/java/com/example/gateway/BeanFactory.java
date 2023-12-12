package com.example.gateway;


import com.example.common.redis.RedisConfig;
import com.example.common.util.JsonObjectConverter;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.util.List;


@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableWebFlux
@Import({RedisConfig.class})
public class BeanFactory implements WebFluxConfigurer {

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs()
                .jackson2JsonDecoder(new Jackson2JsonDecoder(JsonObjectConverter.getObjectMapper()));
        configurer.defaultCodecs()
                .jackson2JsonEncoder(new Jackson2JsonEncoder(JsonObjectConverter.getObjectMapper()));
        configurer.defaultCodecs().maxInMemorySize(64 * 1024 * 1024);
    }

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

    @Bean
    @Primary
    KeyResolver userKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName);
    }

    @Bean
    KeyResolver apiKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
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
    public List<HttpMessageReader<?>> messageReaders(ServerCodecConfigurer codecConfigurer) {
        return codecConfigurer.getReaders();
    }


}
