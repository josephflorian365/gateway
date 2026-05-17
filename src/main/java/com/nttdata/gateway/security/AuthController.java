package com.nttdata.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final String configuredUsername;
    private final String configuredPassword;

    public AuthController(
            JwtService jwtService,
            @Value("${spring.security.user.name:${GATEWAY_USERNAME:admin}}") String configuredUsername,
            @Value("${spring.security.user.password:${GATEWAY_PASSWORD:admin123}}") String configuredPassword) {
        this.jwtService = jwtService;
        this.configuredUsername = configuredUsername;
        this.configuredPassword = configuredPassword;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        if (!configuredUsername.equals(request.username()) || !configuredPassword.equals(request.password())) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        String token = jwtService.generateToken(request.username());
        return Mono.just(ResponseEntity.ok(new AuthResponse(token, jwtService.getExpirationSeconds())));
    }
}
