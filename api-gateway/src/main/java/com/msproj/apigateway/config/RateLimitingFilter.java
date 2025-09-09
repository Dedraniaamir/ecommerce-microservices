package com.msproj.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger; /**
 * Rate Limiting Filter
 * Implements basic rate limiting per client/user
 */
@Component
public class RateLimitingFilter extends AbstractGatewayFilterFactory<RateLimitingFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    private final Map<String, ClientRateLimit> rateLimitStore = new ConcurrentHashMap<>();

    public RateLimitingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String clientId = getClientId(exchange.getRequest());

            ClientRateLimit rateLimit = rateLimitStore.computeIfAbsent(clientId,
                    k -> new ClientRateLimit(config.getLimit(), config.getDuration()));

            if (!rateLimit.allowRequest()) {
                logger.warn("Rate limit exceeded for client: {}", clientId);

                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                response.getHeaders().add("Content-Type", "application/json");
                response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getLimit()));
                response.getHeaders().add("X-RateLimit-Remaining", "0");
                response.getHeaders().add("X-RateLimit-Reset", String.valueOf(rateLimit.getResetTime()));

                String errorBody = """
                    {
                        "error": "rate_limit_exceeded",
                        "message": "Too many requests",
                        "limit": %d,
                        "window": "%s"
                    }
                    """.formatted(config.getLimit(), config.getDuration().toString());

                DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes());
                return response.writeWith(Mono.just(buffer));
            }

            // Add rate limit headers to response
            return chain.filter(exchange)
                    .doOnSuccess(result -> {
                        ServerHttpResponse response = exchange.getResponse();
                        response.getHeaders().add("X-RateLimit-Limit", String.valueOf(config.getLimit()));
                        response.getHeaders().add("X-RateLimit-Remaining",
                                String.valueOf(rateLimit.getRemainingRequests()));
                        response.getHeaders().add("X-RateLimit-Reset",
                                String.valueOf(rateLimit.getResetTime()));
                    });
        };
    }

    private String getClientId(ServerHttpRequest request) {
        // Use different strategies to identify clients
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null) {
            return "auth:" + authHeader.hashCode();
        }

        // Fall back to IP address
        String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null && request.getRemoteAddress() != null) {
            clientIp = request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "ip:" + (clientIp != null ? clientIp : "unknown");
    }

    public static class Config {
        private int limit;
        private Duration duration;

        public Config() {}

        public Config(int limit, Duration duration) {
            this.limit = limit;
            this.duration = duration;
        }

        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }

        public Duration getDuration() { return duration; }
        public void setDuration(Duration duration) { this.duration = duration; }
    }

    private static class ClientRateLimit {
        private final int limit;
        private final Duration window;
        private final AtomicInteger requests = new AtomicInteger(0);
        private volatile long windowStart = System.currentTimeMillis();

        public ClientRateLimit(int limit, Duration window) {
            this.limit = limit;
            this.window = window;
        }

        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();

            // Reset window if expired
            if (now - windowStart >= window.toMillis()) {
                windowStart = now;
                requests.set(0);
            }

            return requests.incrementAndGet() <= limit;
        }

        public int getRemainingRequests() {
            return Math.max(0, limit - requests.get());
        }

        public long getResetTime() {
            return windowStart + window.toMillis();
        }
    }

    @Bean
    public KeyResolver userKeyResolver() {
        // Use client IP as key
        return exchange -> Mono.just(
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
        );
    }
}
