package com.nttdata.gateway.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Gateway Welcome and Health Controller.
 * Provides endpoints for monitoring API Gateway status.
 */
@RestController
@Slf4j
@Tag(name = "Gateway Health", description = "Gateway status and health endpoints")
public class GatewayController {

    /**
     * Welcome endpoint for API Gateway.
     * Returns basic information about the gateway.
     *
     * @return Mono with welcome message
     */
    @GetMapping("/")
    public Mono<ResponseEntity<Map<String, Object>>> welcome() {
        log.debug("API Gateway root endpoint accessed");

        return Mono.fromCallable(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "API Gateway");
            response.put("version", "1.0.0");
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Banking System API Gateway is routing requests");
            response.put("port", 8080);
            response.put("routes", new String[]{
                "/clients - Client Service",
                "/accounts - Product Service",
                "/transactions - Product Service",
                "/credits - Product Service"
            });

            return ResponseEntity.ok(response);
        });
    }

    /**
     * Health check endpoint for gateway.
     * Returns overall gateway health status.
     *
     * @return Mono with health information
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        log.debug("[REACTIVE] Health check endpoint accessed");

        return Mono.fromCallable(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("service", "API Gateway");
            response.put("timestamp", LocalDateTime.now());
            response.put("reactive", true);
            response.put("message", "Gateway is healthy and routing normally");

            return ResponseEntity.ok(response);
        });
    }

    /**
     * Routes info endpoint.
     * Returns information about available routes.
     *
     * @return Mono with routes information
     */
    @GetMapping("/routes")
    public Mono<ResponseEntity<Map<String, Object>>> routes() {
        log.debug("[REACTIVE] Routes information endpoint accessed");

        return Mono.fromCallable(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "API Gateway");
            response.put("version", "1.0.0");
            response.put("timestamp", LocalDateTime.now());

            Map<String, String> routeMap = new HashMap<>();
            routeMap.put("clients", "http://localhost:8081/clients");
            routeMap.put("accounts", "http://localhost:8082/accounts");
            routeMap.put("transactions", "http://localhost:8082/transactions");
            routeMap.put("credits", "http://localhost:8082/credits");

            response.put("routes", routeMap);
            response.put("message", "All routes are configured and ready");

            return ResponseEntity.ok(response);
        });
    }

    /**
     * Info endpoint for gateway.
     * Returns detailed information about the gateway configuration.
     *
     * @return Mono with gateway information
     */
    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> info() {
        log.debug("[REACTIVE] Info endpoint accessed");

        return Mono.fromCallable(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "API Gateway");
            response.put("version", "1.0.0");
            response.put("port", 8080);
            response.put("profiles", "gateway");
            response.put("reactive", true);
            response.put("discoveryClient", "Eureka");
            response.put("configServer", "http://localhost:8888");
            response.put("eurekaServer", "http://localhost:8761");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        });
    }
}

