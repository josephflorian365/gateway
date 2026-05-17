package com.nttdata.gateway.security;

public record AuthResponse(String token, long expiresInSeconds) {
}
