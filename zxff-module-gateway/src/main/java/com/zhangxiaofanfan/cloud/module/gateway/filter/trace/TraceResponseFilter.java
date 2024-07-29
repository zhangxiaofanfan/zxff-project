package com.zhangxiaofanfan.cloud.module.gateway.filter.trace;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.apache.skywalking.apm.toolkit.webflux.WebFluxSkyWalkingOperators;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TraceResponseFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestTraceId = exchange.getRequest().getHeaders().getFirst("X-ZXFF-TRACE-ID");
        String traceId = WebFluxSkyWalkingOperators.continueTracing(exchange, TraceContext::traceId);
        log.info("request traceId: {}, response traceId: {}", requestTraceId, traceId);
        exchange.getResponse().getHeaders().set("X-ZXFF-TRACE-ID", traceId);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
