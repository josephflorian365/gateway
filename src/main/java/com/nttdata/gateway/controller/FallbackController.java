package com.nttdata.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fallback endpoints used by gateway circuit breakers.
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback/client-service")
    public Mono<ResponseEntity<Map<String, Object>>> clientServiceFallback() {
        return Mono.just(buildFallbackResponse("client-service"));
    }

    @GetMapping("/fallback/product-service")
    public Mono<ResponseEntity<Map<String, Object>>> productServiceFallback() {
        return Mono.just(buildFallbackResponse("product-service"));
    }

    private ResponseEntity<Map<String, Object>> buildFallbackResponse(String serviceName) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", serviceName);
        response.put("status", "DEGRADED");
        response.put("message", "The upstream service is temporarily unavailable");
        response.put("timestamp", LocalDateTime.now());
        response.put("timeoutSeconds", 2);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}
