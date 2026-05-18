package com.nttdata.gateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FallbackControllerTest {

    private final FallbackController controller = new FallbackController();

    @Test
    void clientServiceFallbackShouldReturnExpectedPayload() {
        ResponseEntity<Map<String, Object>> response = controller.clientServiceFallback().block();

        assertFallbackResponse(response, "client-service");
    }

    @Test
    void productServiceFallbackShouldReturnExpectedPayload() {
        ResponseEntity<Map<String, Object>> response = controller.productServiceFallback().block();

        assertFallbackResponse(response, "product-service");
    }

    private void assertFallbackResponse(ResponseEntity<Map<String, Object>> response, String serviceName) {
        assertNotNull(response);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(serviceName, response.getBody().get("service"));
        assertEquals("DEGRADED", response.getBody().get("status"));
        assertEquals(2, response.getBody().get("timeoutSeconds"));
        assertTrue(response.getBody().containsKey("timestamp"));
    }
}
