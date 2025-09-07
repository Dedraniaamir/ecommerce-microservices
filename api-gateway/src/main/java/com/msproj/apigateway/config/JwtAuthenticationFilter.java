package com.msproj.apigateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component; /**
 * JWT Authentication Filter
 * Handles JWT token validation and user context setup
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Extract JWT token from Authorization header
            String authHeader = request.getHeaders().getFirst("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.debug("No JWT token found in request to {}", request.getPath());
                return chain.filter(exchange);
            }

            String token = authHeader.substring(7);

            return ReactiveSecurityContextHolder.getContext()
                    .cast(org.springframework.security.core.context.SecurityContext.class)
                    .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
                    .defaultIfEmpty(null)
                    .flatMap(auth -> {
                        if (auth != null && auth.isAuthenticated()) {
                            // Add user information to request headers for downstream services
                            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                    .header("X-User-Name", auth.getName())
                                    .header("X-User-Authorities", auth.getAuthorities().toString())
                                    .build();

                            if (auth.getPrincipal() instanceof Jwt jwt) {
                                mutatedRequest = mutatedRequest.mutate()
                                        .header("X-User-Email", jwt.getClaimAsString("email"))
                                        .header("X-User-ID", jwt.getClaimAsString("sub"))
                                        .build();
                            }

                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        }

                        return chain.filter(exchange);
                    });
        };
    }

    public static class Config {
        private String requiredRole;

        public Config() {}

        public Config(String requiredRole) {
            this.requiredRole = requiredRole;
        }

        public String getRequiredRole() { return requiredRole; }
        public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }
    }
}
