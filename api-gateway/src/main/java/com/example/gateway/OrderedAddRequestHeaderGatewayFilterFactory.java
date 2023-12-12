package com.example.gateway;

import com.example.common.context.ServiceContext;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;


public class OrderedAddRequestHeaderGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return new OrderedGatewayFilter(new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String value = ServerWebExchangeUtils.expand(exchange, config.getValue());
                ServerHttpRequest request = exchange.getRequest().mutate()
                        .headers(httpHeaders -> {
                            // remove this header so when front-end sends the expired token
                            // it won't cause exception in backend services
                            httpHeaders.remove(ServiceContext.TOKEN_HEADER);
                            httpHeaders.add(config.getName(), value);
                        }).build();

                return chain.filter(exchange.mutate().request(request).build());
            }

            @Override
            public String toString() {
                return filterToStringCreator(OrderedAddRequestHeaderGatewayFilterFactory.this)
                        .append(config.getName(), config.getValue()).toString();
            }
        }, Ordered.HIGHEST_PRECEDENCE + 100);
    }

}
