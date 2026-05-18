package com.nttdata.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GatewayControllerTest {

    private final GatewayController controller = new GatewayController();

    @Test
    void welcomeShouldReturnGatewayOverview() {
        ResponseEntity<Map<String, Object>> response = controller.welcome().block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("API Gateway", response.getBody().get("service"));
        assertEquals("UP", response.getBody().get("status"));
        assertEquals(8080, response.getBody().get("port"));
        assertTrue(response.getBody().containsKey("routes"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    void healthShouldReturnReactiveGatewayStatus() {
        ResponseEntity<Map<String, Object>> response = controller.health().block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("API Gateway", response.getBody().get("service"));
        assertEquals("UP", response.getBody().get("status"));
        assertEquals(true, response.getBody().get("reactive"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    void routesShouldDescribeConfiguredRoutes() {
        ResponseEntity<Map<String, Object>> response = controller.routes().block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("API Gateway", response.getBody().get("service"));
        assertTrue(response.getBody().containsKey("routes"));
        assertEquals("All routes are configured and ready", response.getBody().get("message"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }

    @Test
    void infoShouldExposeGatewayConfigurationDetails() {
        ResponseEntity<Map<String, Object>> response = controller.info().block();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("API Gateway", response.getBody().get("service"));
        assertEquals(8080, response.getBody().get("port"));
        assertEquals(true, response.getBody().get("reactive"));
        assertEquals("Eureka", response.getBody().get("discoveryClient"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }
}
