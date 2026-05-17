package com.nttdata.gateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "12345678901234567890123456789012");
        ReflectionTestUtils.setField(jwtService, "expirationSeconds", 3600L);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtService.generateToken("gateway-user");

        assertEquals("gateway-user", jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token));
        assertEquals(3600L, jwtService.getExpirationSeconds());
    }
}
