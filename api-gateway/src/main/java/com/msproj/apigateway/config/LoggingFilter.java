package com.msproj.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Comprehensive Logging Filter
 *
 * Logging Features Demonstrated:
 * 1. Request/Response JSON logging
 * 2. Performance metrics tracking
 * 3. User activity logging
 * 4. Error tracking and correlation
 * 5. Structured logging with MDC
 * 6. Service-specific logging tags
 */
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final Logger accessLogger = LoggerFactory.getLogger("ACCESS_LOG");
    private static final Logger performanceLogger = LoggerFactory.getLogger("PERFORMANCE_LOG");

    private final ObjectMapper objectMapper;

    public LoggingFilter() {
        super(Config.class);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String serviceName = config.getServiceName();
            ServerHttpRequest request = exchange.getRequest();

            // Start timing
            Instant startTime = Instant.now();

            // Extract request information
            String method = request.getMethod().name();
            String path = request.getPath().value();
            String queryParams = formatQueryParams(request.getQueryParams());
            String clientIp = getClientIp(request);
            String userAgent = request.getHeaders().getFirst("User-Agent");
            String correlationId = request.getHeaders().getFirst("X-Correlation-ID");

            // Set up MDC for structured logging
            try (var mdcCloseable = MDC.putCloseable("service", serviceName);
                 var mdcCloseable2 = MDC.putCloseable("correlationId", correlationId);
                 var mdcCloseable3 = MDC.putCloseable("clientIp", clientIp)) {

                // Log incoming request
                logIncomingRequest(serviceName, method, path, queryParams, clientIp, userAgent, request);

                return ReactiveSecurityContextHolder.getContext()
                        .cast(org.springframework.security.core.context.SecurityContext.class)
                        .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
                        .defaultIfEmpty(null)
                        .flatMap(auth -> {
                            // Log user information if authenticated
                            if (auth != null && auth.isAuthenticated()) {
                                logUserActivity(auth, method, path, serviceName);
                            }

                            return chain.filter(exchange);
                        })
                        .doOnSuccess(result -> {
                            // Log successful response
                            Duration duration = Duration.between(startTime, Instant.now());
                            ServerHttpResponse response = exchange.getResponse();

                            logOutgoingResponse(serviceName, method, path,
                                    (HttpStatus) response.getStatusCode(), duration, true);

                            logPerformanceMetrics(serviceName, method, path, duration,
                                    (HttpStatus) response.getStatusCode());
                        })
                        .doOnError(error -> {
                            // Log error response
                            Duration duration = Duration.between(startTime, Instant.now());

                            logErrorResponse(serviceName, method, path, error, duration);

                            logger.error("Request failed for {} {} to service {}: {}",
                                    method, path, serviceName, error.getMessage(), error);
                        });
            }
        };
    }

    private void logIncomingRequest(String serviceName, String method, String path,
                                    String queryParams, String clientIp, String userAgent,
                                    ServerHttpRequest request) {

        Map<String, Object> logData = Map.of(
                "event", "incoming_request",
                "service", serviceName,
                "method", method,
                "path", path,
                "queryParams", queryParams,
                "clientIp", clientIp,
                "userAgent", userAgent,
                "headers", sanitizeHeaders(request.getHeaders()),
                "timestamp", Instant.now().toString()
        );

        try {
            String logJson = objectMapper.writeValueAsString(logData);
            accessLogger.info("INCOMING: {}", logJson);
        } catch (Exception e) {
            logger.warn("Failed to log incoming request: {}", e.getMessage());
        }
    }

    private void logOutgoingResponse(String serviceName, String method, String path,
                                     HttpStatus statusCode, Duration duration, boolean success) {

        Map<String, Object> logData = Map.of(
                "event", "outgoing_response",
                "service", serviceName,
                "method", method,
                "path", path,
                "statusCode", statusCode != null ? statusCode.value() : 0,
                "duration", duration.toMillis(),
                "success", success,
                "timestamp", Instant.now().toString()
        );

        try {
            String logJson = objectMapper.writeValueAsString(logData);
            accessLogger.info("OUTGOING: {}", logJson);
        } catch (Exception e) {
            logger.warn("Failed to log outgoing response: {}", e.getMessage());
        }
    }

    private void logUserActivity(Authentication auth, String method, String path, String serviceName) {
        String username = auth.getName();
        String authorities = auth.getAuthorities().toString();

        // Extract additional user info from JWT if available
        String email = null;
        String userId = null;

        if (auth.getPrincipal() instanceof Jwt jwt) {
            email = jwt.getClaimAsString("email");
            userId = jwt.getClaimAsString("sub");
        }

        Map<String, Object> activityLog = Map.of(
                "event", "user_activity",
                "username", username,
                "userId", userId != null ? userId : "unknown",
                "email", email != null ? email : "unknown",
                "authorities", authorities,
                "action", method + " " + path,
                "service", serviceName,
                "timestamp", Instant.now().toString()
        );

        try {
            String logJson = objectMapper.writeValueAsString(activityLog);
            logger.info("USER_ACTIVITY: {}", logJson);
        } catch (Exception e) {
            logger.warn("Failed to log user activity: {}", e.getMessage());
        }
    }

    private void logPerformanceMetrics(String serviceName, String method, String path,
                                       Duration duration, HttpStatus statusCode) {

        Map<String, Object> performanceData = Map.of(
                "event", "performance_metric",
                "service", serviceName,
                "endpoint", method + " " + path,
                "duration_ms", duration.toMillis(),
                "status_code", statusCode != null ? statusCode.value() : 0,
                "timestamp", Instant.now().toString()
        );

        try {
            String logJson = objectMapper.writeValueAsString(performanceData);
            performanceLogger.info("PERFORMANCE: {}", logJson);
        } catch (Exception e) {
            logger.warn("Failed to log performance metrics: {}", e.getMessage());
        }
    }

    private void logErrorResponse(String serviceName, String method, String path,
                                  Throwable error, Duration duration) {

        Map<String, Object> errorLog = Map.of(
                "event", "error_response",
                "service", serviceName,
                "method", method,
                "path", path,
                "error_type", error.getClass().getSimpleName(),
                "error_message", error.getMessage(),
                "duration", duration.toMillis(),
                "timestamp", Instant.now().toString()
        );

        try {
            String logJson = objectMapper.writeValueAsString(errorLog);
            logger.error("ERROR_RESPONSE: {}", logJson);
        } catch (Exception e) {
            logger.warn("Failed to log error response: {}", e.getMessage());
        }
    }

    private String formatQueryParams(MultiValueMap<String, String> queryParams) {
        if (queryParams.isEmpty()) {
            return "";
        }
        return queryParams.toString();
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private Map<String, String> sanitizeHeaders(org.springframework.http.HttpHeaders headers) {
        Map<String, String> sanitized = new ConcurrentHashMap<>();

        headers.forEach((name, values) -> {
            // Don't log sensitive headers
            if (name.toLowerCase().contains("authorization") ||
                    name.toLowerCase().contains("cookie") ||
                    name.toLowerCase().contains("password")) {
                sanitized.put(name, "[REDACTED]");
            } else {
                sanitized.put(name, String.join(", ", values));
            }
        });

        return sanitized;
    }

    public static class Config {
        private String serviceName;

        public Config() {}

        public Config(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    }
}


