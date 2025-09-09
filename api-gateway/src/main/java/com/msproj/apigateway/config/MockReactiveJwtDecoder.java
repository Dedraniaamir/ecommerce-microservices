package com.msproj.apigateway.config;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Mock JWT Decoder for local development
 *
 * Always returns a fake JWT with predefined roles and claims.
 * Useful when Keycloak is not available.
 */
public class MockReactiveJwtDecoder implements ReactiveJwtDecoder {

    @Override
    public Mono<Jwt> decode(String token) {
        // Generate a mock JWT with claims
        Jwt jwt = new Jwt(
                token != null ? token : UUID.randomUUID().toString(), // token value
                Instant.now(),                                        // issued at
                Instant.now().plusSeconds(3600),                      // expires in 1h
                Map.of("alg", "none"),                                // headers
                Map.of(
                        "sub", "mock-user",
                        "preferred_username", "mockuser",
                        "email", "mockuser@example.com",
                        "realm_access", Map.of(
                                "roles", Collections.singletonList("USER") // default role
                        )
                )
        );

        return Mono.just(jwt);
    }
}

