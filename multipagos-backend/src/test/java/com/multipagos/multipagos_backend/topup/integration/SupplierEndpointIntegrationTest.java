package com.multipagos.multipagos_backend.topup.integration;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SupplierEndpointIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SupplierEndpointIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetSuppliersEndpoint() {
        String url = "http://localhost:" + port + "/api/v1/suppliers";

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify standardized response format
        assertTrue(response.getBody().contains("\"status\":\"success\""));
        assertTrue(response.getBody().contains("\"message\":\"Suppliers retrieved successfully\""));
        assertTrue(response.getBody().contains("\"data\":["));
        assertTrue(response.getBody().contains("\"apiVersion\":\"v1\""));
        assertTrue(response.getBody().contains("\"timestamp\""));

        // Verify supplier data is present
        assertTrue(response.getBody().contains("Claro") || response.getBody().contains("Movistar"));

        logger.info("✅ Endpoint Response: {}", response.getBody());
    }
}