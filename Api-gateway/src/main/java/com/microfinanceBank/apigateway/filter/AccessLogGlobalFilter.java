package com.microfinanceBank.apigateway.filter;

import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AccessLogGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange).then(Mono.deferContextual(contextView -> {
            String traceId = null;
            String spanId = null;
            if (contextView.hasKey(TraceContext.class)) {
                TraceContext traceContext = contextView.get(TraceContext.class);
                traceId = traceContext.traceIdString();
                spanId = traceContext.spanIdString();
            }
            try {
                if (traceId != null) {
                    MDC.put("traceId", traceId);
                    MDC.put("spanId", spanId);
                }
                log.info("{} {} -> {} ({}ms)",
                        request.getMethod(),
                        request.getURI().getPath(),
                        exchange.getResponse().getStatusCode(),
                        System.currentTimeMillis() - startTime);
            } finally {
                MDC.remove("traceId");
                MDC.remove("spanId");
            }
            return Mono.empty();
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
