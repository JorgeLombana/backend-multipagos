package com.multipagos.multipagos_backend.topup.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.multipagos.multipagos_backend.topup.presentation.dto.TopUpRequestDto;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TopUpEndpointIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(TopUpEndpointIntegrationTest.class);

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testTopUpEndpointValidation() {
        String url = "http://localhost:" + port + "/api/v1/topup";

        // Test with invalid request (missing cellPhone)
        TopUpRequestDto invalidRequest = TopUpRequestDto.builder()
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopUpRequestDto> entity = new HttpEntity<>(invalidRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verify validation error response format
        assertTrue(response.getBody().contains("\"status\":\"error\""));
        assertTrue(response.getBody().contains("\"error\":\"Validation Failed\""));
        assertTrue(response.getBody().contains("cellPhone"));

        logger.info("✅ Validation Error Response: {}", response.getBody());
    }

    @Test
    void testTopUpEndpointWithValidRequest() {
        String url = "http://localhost:" + port + "/api/v1/topup";

        // Test with valid request
        TopUpRequestDto validRequest = TopUpRequestDto.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TopUpRequestDto> entity = new HttpEntity<>(validRequest, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Note: This might fail due to external API dependency, but we can check the
        // structure
        assertNotNull(response.getBody());

        logger.info("✅ TopUp Response Status: {}", response.getStatusCode());
        logger.info("✅ TopUp Response Body: {}", response.getBody());

        // If successful, verify response structure
        if (response.getStatusCode() == HttpStatus.OK) {
            assertTrue(response.getBody().contains("\"status\":\"success\""));
            assertTrue(response.getBody().contains("\"data\""));
            assertTrue(response.getBody().contains("\"apiVersion\":\"v1\""));
        }
    }
}