package com.msproj.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono; /**
 * Global Correlation ID Filter
 * Adds correlation IDs for request tracing across services
 */
@Component
public class GlobalCorrelationIdFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(GlobalCorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);

        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId();
        }

        // Add to request headers for downstream services
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, correlationId)
                .build();

        // Add to response headers
        exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, correlationId);

        // Add to MDC for logging
        try (MDC.MDCCloseable mdcCloseable = MDC.putCloseable("correlationId", correlationId)) {
            logger.debug("Processing request with correlation ID: {}", correlationId);

            return chain.filter(exchange.mutate()
                    .request(mutatedRequest)
                    .build());
        }
    }

    @Override
    public int getOrder() {
        return -100; // High priority - execute early in the filter chain
    }

    private String generateCorrelationId() {
        return "GW-" + System.currentTimeMillis() + "-" +
                Integer.toHexString((int)(Math.random() * 0x10000));
    }
}
