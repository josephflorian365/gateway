package com.nttdata.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthenticationFilter(jwtService);
    }

    @Test
    void shouldAllowWhitelistedPathWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/auth/login").build()
        );
        AtomicBoolean chainInvoked = new AtomicBoolean(false);
        WebFilterChain chain = ignored -> {
            chainInvoked.set(true);
            return Mono.empty();
        };

        filter.filter(exchange, chain).block();

        assertTrue(chainInvoked.get());
        assertNull(exchange.getResponse().getStatusCode());
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/clients").build()
        );

        filter.filter(exchange, ignored -> Mono.empty()).block();

        assertUnauthorized(exchange.getResponse());
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthorizationHeaderDoesNotUseBearerScheme() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/clients")
                .header(HttpHeaders.AUTHORIZATION, "Basic abc123")
                .build()
        );

        filter.filter(exchange, ignored -> Mono.empty()).block();

        assertUnauthorized(exchange.getResponse());
        verifyNoInteractions(jwtService);
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsInvalid() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token")
                .build()
        );
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        filter.filter(exchange, ignored -> Mono.empty()).block();

        assertUnauthorized(exchange.getResponse());
        verify(jwtService).isTokenValid("invalid-token");
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenValidationThrowsException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer broken-token")
                .build()
        );
        when(jwtService.isTokenValid("broken-token")).thenThrow(new IllegalArgumentException("Invalid token"));

        filter.filter(exchange, ignored -> Mono.empty()).block();

        assertUnauthorized(exchange.getResponse());
        verify(jwtService).isTokenValid("broken-token");
    }

    @Test
    void shouldPopulateSecurityContextWhenTokenIsValid() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/clients")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid-token")
                .build()
        );
        AtomicReference<String> authenticatedUsername = new AtomicReference<>();
        WebFilterChain chain = ignored -> ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .doOnNext(authenticatedUsername::set)
            .then();
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractUsername("valid-token")).thenReturn("gateway-user");

        filter.filter(exchange, chain).block();

        assertEquals("gateway-user", authenticatedUsername.get());
        assertNull(exchange.getResponse().getStatusCode());
        verify(jwtService).isTokenValid("valid-token");
        verify(jwtService).extractUsername("valid-token");
    }

    private void assertUnauthorized(ServerHttpResponse response) {
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
