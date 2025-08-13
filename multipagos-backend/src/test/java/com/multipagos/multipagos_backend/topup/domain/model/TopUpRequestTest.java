package com.multipagos.multipagos_backend.topup.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TopUpRequestTest {

    @Test
    void shouldValidateValidRequest() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        assertTrue(request.isValid());
        assertNull(request.getCellPhoneValidationError());
        assertNull(request.getValueValidationError());
        assertNull(request.getSupplierIdValidationError());
    }

    @Test
    void shouldValidateInvalidCellPhone() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("2001234567") // Should start with 3
                .value(new BigDecimal("5000"))
                .supplierId("8753")
                .build();

        assertFalse(request.isValid());
        assertNotNull(request.getCellPhoneValidationError());
        assertTrue(request.getCellPhoneValidationError().contains("debe empezar con 3"));
    }

    @Test
    void shouldValidateInvalidValue() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("500")) // Below minimum
                .supplierId("8753")
                .build();

        assertFalse(request.isValid());
        assertNotNull(request.getValueValidationError());
        assertTrue(request.getValueValidationError().contains("mayor o igual a 1000"));
    }

    @Test
    void shouldValidateInvalidSupplierId() {
        TopUpRequest request = TopUpRequest.builder()
                .cellPhone("3001234567")
                .value(new BigDecimal("5000"))
                .supplierId("9999") // Invalid supplier
                .build();

        assertFalse(request.isValid());
        assertNotNull(request.getSupplierIdValidationError());
        assertTrue(request.getSupplierIdValidationError().contains("debe ser uno de"));
    }

    @Test
    void shouldValidateNullValues() {
        TopUpRequest request = TopUpRequest.builder().build();

        assertFalse(request.isValid());
        assertNotNull(request.getCellPhoneValidationError());
        assertNotNull(request.getValueValidationError());
        assertNotNull(request.getSupplierIdValidationError());
    }
}