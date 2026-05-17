package com.nttdata.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private JwtService jwtService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        authController = new AuthController(jwtService, "admin", "secret");
    }

    @Test
    void shouldReturnJwtWhenCredentialsAreValid() {
        when(jwtService.generateToken("admin")).thenReturn("jwt-token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        ResponseEntity<AuthResponse> response = authController.login(new AuthRequest("admin", "secret")).block();

        assertTrue(isSuccessfulAuth(response, "jwt-token", 3600L));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        ResponseEntity<AuthResponse> response =
            authController.login(new AuthRequest("admin", "bad-password")).block();

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private boolean isSuccessfulAuth(ResponseEntity<AuthResponse> response, String token, long expirationSeconds) {
        return response.getStatusCode() == HttpStatus.OK
            && response.getBody() != null
            && token.equals(response.getBody().token())
            && expirationSeconds == response.getBody().expiresInSeconds();
    }
}
